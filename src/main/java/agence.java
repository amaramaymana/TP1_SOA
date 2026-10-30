import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

public class agence {
    private static final String XML_FILE = "/agence.xml";
    private static final String XML_INVALIDE = "/agence_invalide.xml";
    private static final String XSD_FILE = "/agence.xsd";

    public static void main(String[] args) throws Exception {
        System.out.println("1. VALIDATION XML / XSD ");

        System.out.println("\nCas valide : agence.xml");
        boolean valide = validerXML(XML_FILE, XSD_FILE);

        System.out.println("\nCas invalide : agence_invalide.xml ");
        validerXML(XML_INVALIDE, XSD_FILE);

        if (!valide) {
            System.out.println("\nLe document principal est invalide : arrêt du programme.");
            return;
        }

        System.out.println("\n2. INTERROGATION XPATH ");
        interrogerXPath(XML_FILE);
    }
    public static boolean validerXML(String xmlPath, String xsdPath) {
        try (InputStream xml = agence.class.getResourceAsStream(xmlPath);
             InputStream xsd = agence.class.getResourceAsStream(xsdPath)) {

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();

            final int[] nbErreurs = {0};
            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {
                    System.out.println("   Avertissement : " + e.getMessage());
                }

                @Override
                public void error(SAXParseException e) {
                    nbErreurs[0]++;
                    System.out.println("   Erreur " + nbErreurs[0] + " (ligne " + e.getLineNumber() + ") : " + e.getMessage());
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    throw e;
                }
            });

            validator.validate(new StreamSource(xml));

            if (nbErreurs[0] == 0) {
                System.out.println("OK : le document XML est valide par rapport au schéma XSD.");
                return true;
            } else {
                System.out.println("INVALIDE : " + nbErreurs[0] + " erreur(s) trouvée(s).");
                return false;
            }

        } catch (SAXException e) {
            System.out.println("ERREUR de validation : " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("ERREUR d'accès aux fichiers : " + e);
            return false;
        }
    }

    public static void interrogerXPath(String xmlPath) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(agence.class.getResourceAsStream(xmlPath));

        XPath xpath = XPathFactory.newInstance().newXPath();

        afficher(xpath, doc, "Q1 - Marques de toutes les voitures",
                "//voiture/marque");

        afficher(xpath, doc, "Q2 - Voitures disponibles (modèles)",
                "//voiture[@disponible='true']/modele");

        afficher(xpath, doc, "Q3 - Voitures en promotion (modèles)",
                "//voiture[promotion='true']/modele");

        afficher(xpath, doc, "Q4 - Clients actifs (noms)",
                "//client[@actif='true']/nom");

        afficher(xpath, doc, "Q5 - Voitures dont le prix par jour > 100 DT (modèles)",
                "//voiture[prixJour > 100]/modele");

        afficher(xpath, doc, "Q6 - Locations en cours (identifiants)",
                "//location[statut='en_cours']/@id");

        XPathExpression expr = xpath.compile("count(//voiture)");
        Double nb = (Double) expr.evaluate(doc, XPathConstants.NUMBER);
        System.out.println("\nQ7 - Nombre total de voitures : " + nb.intValue());

        expr = xpath.compile("sum(//location/montantTotal)");
        Double total = (Double) expr.evaluate(doc, XPathConstants.NUMBER);
        System.out.println("Q8 - Chiffre d'affaires total des locations : " + total + " DT");
    }

    private static void afficher(XPath xpath, Document doc, String titre, String requete) throws Exception {
        XPathExpression expr = xpath.compile(requete);
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

        System.out.println("\n" + titre + "  [" + requete + "]");
        if (nodes.getLength() == 0) {
            System.out.println("   (aucun résultat)");
        }
        for (int i = 0; i < nodes.getLength(); i++) {
            System.out.println("   - " + nodes.item(i).getTextContent());
        }
    }
}