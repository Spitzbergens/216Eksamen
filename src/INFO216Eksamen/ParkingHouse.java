package INFO216Eksamen;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;

public class ParkingHouse {

    /**
     * Scenario: A parking house is a kind of building and has unique (one and only one) capacity for a specific number
     * of cars. CityGarage is a Parking House. It can take 225 cars. All buildings and cars have unique locations.
     * SV27564 is a car that is currently parked in the CityGarage.
     * @param args
     */

    public static void main(String[] args) {
        createOntology();

        System.out.println("//////////////////");

        createOntologyInsert();
    }


    private static void createOntology(){
        OntModel ontModel = ModelFactory.createOntologyModel();

        String base = "https://ontology.com/";

        Resource parkingHouse = ontModel.createResource(base + "ParkingHouse");
        Resource building = ontModel.createResource(base + "Building");
        Resource cityGarage = ontModel.createResource(base + "CityGarage");
        Resource location = ontModel.createResource(base + "Location");
        Resource aCar = ontModel.createResource(base + "SV27564");
        Resource car = ontModel.createResource(base + "Car");




        Property locationProp = ontModel.createProperty(base + "hasLocation");
        Property capacity = ontModel.createProperty(base + "hasCarCapacity");

//        Resource restrictionCapacity = ontModel.createRestriction("ParkingHouseCapacity", capacity);
//        Resource restrictionLocation = ontModel.createRestriction("LocationRestriction", locationProp);


        parkingHouse.addProperty(RDF.type, building);


        capacity.addProperty(OWL.disjointWith, capacity);
        parkingHouse.addProperty(OWL.disjointWith, parkingHouse);


        cityGarage.addProperty(RDF.type, parkingHouse);
        cityGarage.addLiteral(capacity, 225);

        aCar.addProperty(RDF.type, car);
        aCar.addProperty(locationProp, cityGarage);
        car.addProperty(OWL.disjointWith, car);
//
//        restrictionCapacity.addProperty(OWL.allValuesFrom, parkingHouse);
//        restrictionLocation.addProperty(OWL.allValuesFrom, location);


        ontModel.write(System.out, "TURTLE");

    }

    private static void createOntologyInsert(){

        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        String base = "http://example.com#";
        String prefix =
                "PREFIX base: " + "<" +  base + "> " +" " +
                "PREFIX owl: " + "<" +  OWL.getURI() + "> " +
                "PREFIX rdf: " + "<" +  RDF.getURI() + "> ";

        UpdateAction.parseExecute(
                prefix +
                        "INSERT DATA {" +
                        "base:ParkingHouse a base:Buidling. " +
                        "owl:Restriction owl:onProperty base:hasCarCapacity; " +
                        "                owl:allValuesFrom base:ParkingHouse. " +
                        "owl:Restriction owl:onProperty base:hasLocation; " +
                        "                owl:allValuesFrom base:Location. " +
                        "base:CityGarage a base:ParkingHouse; " +
                        "               base:hasCarCapacity 225." +
                        "base:SV27564 rdf:Type base:Car. " +
                        "base:SV27564 base:hasLocation base:CityGarage. "+
                        "}", ontModel
        );

        ontModel.getWriter("Turtle").write(ontModel, System.out, base);

    }

}
