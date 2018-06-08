package INFO216Eksamen;

import org.apache.jena.ontology.*;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

public class ParkingExtended {


    // 1. Model the scenario described below as an OWL ontology. You can draw some of it as a graph (use a
    //separate paper sheet if you want) and write other parts in Turtle or using Description Logics. Reuse terms
    //from common vocabularies when you can.
    //2. Assume that your ontology is stored in a database that supports RDFS and OWL entailment. Write a
    //SPARQL query that lists all buses that currently have too many passengers.
    //3. Write a SPARQL query that identifies the following inconsistency: a person is in a vehicle, but the location of
    //the person is different from the location of the vehicle.

    // Scenario: A location has a longitude, a latitude and an altitude. A vehicle can be a bicycle, bus or a car. A car can
    //be a private car or a taxi. A building has a unique (one and only one) location. A parking house is a kind of building
    //and has capacity for a specific number of cars. Bicycle stands, bus stops and taxi stops are locations. A person
    //can use a vehicle. A car can be inside a parking house. A bicycle can be in a stand. A bus can be at a bus stop. A
    //taxi can be at a taxi stop. Bicycles, buses and cars are disjoint. A person and a vehicle has a unique (one and only
    //one) location at any particular time. A taxi can have up to 4 passengers, whereas a bus can have up to a specific
    //number of passengers.

    static String base = "http://ontology.com/#";
   static String prefix = "PREFIX base: " + base + "" +
            "PREFIX foaf: <" + FOAF.getURI() + "> ";

    public static void main(String[] args) {
    model();
    }

    private static void model(){
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
        Dataset ds = TDBFactory.createDataset("data");
        Model model = ds.getDefaultModel();
        ontModel.add(model);
        model.begin();


        // Creating main resources
        OntClass Location = ontModel.createClass(base + "Location");
        OntClass Vehicle = ontModel.createClass(base + "Vehicle");
        OntClass Building = ontModel.createClass(base +"Building");
        OntClass ParkingHouse = ontModel.createClass(base + "ParkingHouse");

        // Subtypes Vehicle
        OntClass Bicycle = ontModel.createClass(base+ "Bicycle");
        OntClass Car = ontModel.createClass(base+"Car");
        OntClass Bus = ontModel.createClass(base+ "Bus");

        Vehicle.addSubClass(Bicycle);
        Vehicle.addSubClass(Car);
        Vehicle.addSubClass(Bus);

        // SubTypes Location
        Individual BicycleStand = Location.createIndividual(base+ "BicycleStand");
        Individual BusStop = Location.createIndividual(base+ "BusStop");
        Individual TaxiStop = Location.createIndividual(base+ "TaxiStop");

        // Sub-Sub-Types of Car
        Individual Taxi = ontModel.createIndividual(base+ "Taxi", Car);
        Individual PrivateCar = ontModel.createIndividual(base+ "PrivateCar", Car);

        // Subclasses of Building

        Individual Mats = ontModel.createIndividual(base + "Mats", FOAF.Person);

        // General properties
        Property hasLat = ontModel.createObjectProperty(base + "hasLat");
        ((ObjectProperty) hasLat).addRange(XSD.xfloat);
        Property hasLong = ontModel.createObjectProperty( base + "hasLong");
        Property hasAlt = ontModel.createObjectProperty(base + "hasAlt");
        Property hasLocation = ontModel.createObjectProperty(base + "hasLocation");
        Property carCapacity = ontModel.createObjectProperty(base + "carCapacity");
        Property hasVehicle = ontModel.createObjectProperty(base + "hasVehicle");
        Property noOfSeats = ontModel.createObjectProperty(base + "noOfSeats");
        Property busSeats = ontModel.createObjectProperty(base + "totalNoOfBusSeats");


        // Restrictions on carseats
        Restriction seats = ontModel.createRestriction(noOfSeats);
        seats.convertToCardinalityRestriction(4);

        // Restrictions on bus-seats
        Restriction busseats = ontModel.createRestriction(busSeats);
        busseats.convertToCardinalityRestriction(40);

        // Properties specific for buildings
        Building.addProperty(hasLocation, Location);
        Building.addSubClass(ParkingHouse);
        Building.addProperty(RDF.type, OWL.FunctionalProperty);

        // Properties specific for Location
        Location.addProperty(hasLat, XSD.xfloat);
        Location.addProperty(hasLong, XSD.xfloat);
        Location.addProperty(hasAlt, XSD.xfloat);
        Location.addProperty(RDF.type, "geo:Lat");
        hasLocation.addProperty(RDF.type, OWL.FunctionalProperty);

        Car.addProperty(hasLocation, ParkingHouse);

        ParkingHouse.addProperty(carCapacity, XSD.integer);

        // Properties specific for a person
        Mats.addProperty(hasVehicle, Bicycle);

        // Bicycle can be in a bicycle stand
        Bicycle.addProperty(hasLocation, BicycleStand);
        Mats.addProperty(hasLocation, BicycleStand);
        Mats.addProperty(hasLocation, BusStop);

        // A bus can be in a busstop
        Bus.addProperty(hasLocation, BusStop);

        // A taxi can be in a taxistop
        Taxi.addProperty(hasLocation, TaxiStop);

        Car.addDisjointWith(Bus);
        Car.addDisjointWith(Bicycle);

        ontModel.write(System.out, "TURTLE");


    }

    public static void updateModel(){
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        String query = "INSERT DATA {" +
                "base:Vehicle a schema:Vehicle." +
                "base:Bicycle a base:Vehicle." +
                "base:Car a base:Vehicle." +
                "base:Bus a base:Vehicle. " +
                "base:Building a base:Location;" +
                "           base:HasLong  + " +
                "}";

        UpdateAction.parseExecute(prefix, ontModel);
    }

}
