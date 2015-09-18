package org.opengis.cite.sta10.filteringExtension;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opengis.cite.sta10.SuiteAttribute;
import org.opengis.cite.sta10.util.*;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Includes various tests of capability 3.
 */
public class Capability3Tests {

    public String rootUri;//="http://localhost:8080/OGCSensorThings/v1.0";

    long thingId1, thingId2,
            datastreamId1, datastreamId2, datastreamId3, datastreamId4,
            locationId1, locationId2, historicalLocationId1,
            historicalLocationId2, historicalLocationId3, historicalLocationId4,
            sensorId1, sensorId2, sensorId3, sensorId4,
            observedPropertyId1, observedPropertyId2, observedPropertyId3,
            observationId1, observationId2, observationId3, observationId4, observationId5, observationId6, observationId7, observationId8, observationId9, observationId10, observationId11, observationId12,
            featureOfInterestId1, featureOfInterestId2;


    @BeforeClass
    public void obtainTestSubject(ITestContext testContext) {
        Object obj = testContext.getSuite().getAttribute(
                SuiteAttribute.LEVEL.getName());
        if ((null != obj)) {
            Integer level = Integer.class.cast(obj);
            Assert.assertTrue(level.intValue() > 2,
                    "Conformance level 3 will not be checked since ics = " + level);
        }

        rootUri = testContext.getSuite().getAttribute(
                SuiteAttribute.TEST_SUBJECT.getName()).toString();
        rootUri = rootUri.trim();
        if (rootUri.lastIndexOf('/') == rootUri.length() - 1) {
            rootUri = rootUri.substring(0, rootUri.length() - 1);
        }
        createEntities();
    }


    @Test(description = "GET Entities with $select", groups = "level-3")
    public void readEntitiesWithSelectQO() {
        checkSelectForEntityType(EntityType.THING);
        checkSelectForEntityType(EntityType.LOCATION);
        checkSelectForEntityType(EntityType.HISTORICAL_LOCATION);
        checkSelectForEntityType(EntityType.DATASTREAM);
        checkSelectForEntityType(EntityType.SENSOR);
        checkSelectForEntityType(EntityType.OBSERVED_PROPERTY);
        checkSelectForEntityType(EntityType.OBSERVATION);
        checkSelectForEntityType(EntityType.FEATURE_OF_INTEREST);
    }

    @Test(description = "GET Entities with $expand", groups = "level-3")
    public void readEntitiesWithExpandQO() {
        checkExpandtForEntityType(EntityType.THING);
        checkExpandtForEntityType(EntityType.LOCATION);
        checkExpandtForEntityType(EntityType.HISTORICAL_LOCATION);
        checkExpandtForEntityType(EntityType.DATASTREAM);
        checkExpandtForEntityType(EntityType.SENSOR);
        checkExpandtForEntityType(EntityType.OBSERVED_PROPERTY);
        checkExpandtForEntityType(EntityType.OBSERVATION);
        checkExpandtForEntityType(EntityType.FEATURE_OF_INTEREST);
    }

    @Test(description = "GET Entities with $top", groups = "level-3")
    public void readEntitiesWithTopQO() {
        checkTopForEntityType(EntityType.THING);
        checkTopForEntityType(EntityType.LOCATION);
        checkTopForEntityType(EntityType.HISTORICAL_LOCATION);
        checkTopForEntityType(EntityType.DATASTREAM);
        checkTopForEntityType(EntityType.SENSOR);
        checkTopForEntityType(EntityType.OBSERVED_PROPERTY);
        checkTopForEntityType(EntityType.OBSERVATION);
        checkTopForEntityType(EntityType.FEATURE_OF_INTEREST);
    }

    @Test(description = "GET Entities with $skip", groups = "level-3")
    public void readEntitiesWithSkipQO() {
        checkSkipForEntityType(EntityType.THING);
        checkSkipForEntityType(EntityType.LOCATION);
        checkSkipForEntityType(EntityType.HISTORICAL_LOCATION);
        checkSkipForEntityType(EntityType.DATASTREAM);
        checkSkipForEntityType(EntityType.SENSOR);
        checkSkipForEntityType(EntityType.OBSERVED_PROPERTY);
        checkSkipForEntityType(EntityType.OBSERVATION);
        checkSkipForEntityType(EntityType.FEATURE_OF_INTEREST);
        checkSkipForEntityTypeRelation(EntityType.THING);
        checkSkipForEntityTypeRelation(EntityType.LOCATION);
        checkSkipForEntityTypeRelation(EntityType.HISTORICAL_LOCATION);
        checkSkipForEntityTypeRelation(EntityType.DATASTREAM);
        checkSkipForEntityTypeRelation(EntityType.SENSOR);
        checkSkipForEntityTypeRelation(EntityType.OBSERVED_PROPERTY);
        checkSkipForEntityTypeRelation(EntityType.OBSERVATION);
        checkSkipForEntityTypeRelation(EntityType.FEATURE_OF_INTEREST);

    }

    @Test(description = "GET Entities with $orderby", groups = "level-3")
    public void readEntitiesWithOrderbyQO() {
        checkOrderbyForEntityType(EntityType.THING);
        checkOrderbyForEntityType(EntityType.LOCATION);
        checkOrderbyForEntityType(EntityType.HISTORICAL_LOCATION);
        checkOrderbyForEntityType(EntityType.DATASTREAM);
        checkOrderbyForEntityType(EntityType.SENSOR);
        checkOrderbyForEntityType(EntityType.OBSERVED_PROPERTY);
        checkOrderbyForEntityType(EntityType.OBSERVATION);
        checkOrderbyForEntityType(EntityType.FEATURE_OF_INTEREST);
        checkOrderbyForEntityTypeRelations(EntityType.THING);
        checkOrderbyForEntityTypeRelations(EntityType.LOCATION);
        checkOrderbyForEntityTypeRelations(EntityType.HISTORICAL_LOCATION);
        checkOrderbyForEntityTypeRelations(EntityType.DATASTREAM);
        checkOrderbyForEntityTypeRelations(EntityType.SENSOR);
        checkOrderbyForEntityTypeRelations(EntityType.OBSERVED_PROPERTY);
        checkOrderbyForEntityTypeRelations(EntityType.OBSERVATION);
        checkOrderbyForEntityTypeRelations(EntityType.FEATURE_OF_INTEREST);
    }

    private void checkOrderbyForEntityTypeRelations(EntityType entityType) {
        String[] relations = EntityRelations.getRelationsListFor(entityType);
        try {
            String urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, null);
            Map<String, Object> responseMap = HTTPMethods.doGet(urlString);
            String response = responseMap.get("response").toString();
            JSONArray array = new JSONObject(response).getJSONArray("value");
            if (array.length() == 0) {
                return;
            }
            long id = array.getJSONObject(0).getLong(ControlInformation.ID);

            for (String relation : relations) {
                if (relation.charAt(relation.length() - 1) != 's' && !relation.equals("FeaturesOfInteret")) {
                    continue;
                }
                String[] properties = EntityProperties.getPropertiesListFor(relation);
                EntityType relationEntityType = getEntityTypeFor(relation);
                //single orderby
                for (String property : properties) {
                    if (property.equals("unitOfMeasurement")) {
                        continue;
                    }
                    urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, id, relationEntityType, "?$orderby=" + property);
                    responseMap = HTTPMethods.doGet(urlString);
                    response = responseMap.get("response").toString();
                    array = new JSONObject(response).getJSONArray("value");
                    for (int i = 1; i < array.length(); i++) {
                        JSONObject obj1 = array.getJSONObject(i - 1);
                        JSONObject obj2 = array.getJSONObject(i);
                        Assert.assertTrue(obj2.get(property).toString().compareTo(obj1.get(property).toString()) >= 0, "The ordering is not correct for EntityType " + entityType + " orderby property " + property);
                    }
                    urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, id, relationEntityType, "?$orderby=" + property + "%20asc");
                    responseMap = HTTPMethods.doGet(urlString);
                    response = responseMap.get("response").toString();
                    array = new JSONObject(response).getJSONArray("value");
                    for (int i = 1; i < array.length(); i++) {
                        JSONObject obj1 = array.getJSONObject(i - 1);
                        JSONObject obj2 = array.getJSONObject(i);
                        Assert.assertTrue(obj2.get(property).toString().compareTo(obj1.get(property).toString()) >= 0, "The ordering is not correct for EntityType " + entityType + " orderby asc property " + property);
                    }
                    urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, id, relationEntityType, "?$orderby=" + property + "%20desc");
                    responseMap = HTTPMethods.doGet(urlString);
                    response = responseMap.get("response").toString();
                    array = new JSONObject(response).getJSONArray("value");
                    for (int i = 1; i < array.length(); i++) {
                        JSONObject obj1 = array.getJSONObject(i - 1);
                        JSONObject obj2 = array.getJSONObject(i);
                        Assert.assertTrue(obj1.get(property).toString().compareTo(obj2.get(property).toString()) >= 0, "The ordering is not correct for EntityType " + entityType + " orderby desc property " + property);
                    }
                }

                //multiple orderby
                List<String> orderbyPropeties = new ArrayList<>();
                String orderby = "?$orderby=";
                String orderbyAsc = "?$orderby=";
                String orderbyDesc = "?$orderby=";
                for (String property : properties) {
                    if (property.equals("unitOfMeasurement")) {
                        continue;
                    }
                    if (orderby.charAt(orderby.length() - 1) != '=') {
                        orderby += ",";
                    }
                    orderby += property;
                    orderbyPropeties.add(property);
                    urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, id, relationEntityType, orderby);
                    responseMap = HTTPMethods.doGet(urlString);
                    response = responseMap.get("response").toString();
                    array = new JSONObject(response).getJSONArray("value");
                    for (int i = 1; i < array.length(); i++) {
                        JSONObject obj1 = array.getJSONObject(i - 1);
                        JSONObject obj2 = array.getJSONObject(i);
                        for (int j = 0; j < orderbyPropeties.size(); j++) {
                            String orderbyProperty = orderbyPropeties.get(j);
                            Assert.assertTrue(obj2.get(orderbyProperty).toString().compareTo(obj1.get(orderbyProperty).toString()) >= 0, "The ordering is not correct for EntityType " + entityType + " orderby property " + orderbyProperty);
                            if (obj2.get(orderbyProperty).toString().compareTo(obj1.get(orderbyProperty).toString()) != 0) {
                                break;
                            }
                        }
                    }
                    if (orderbyAsc.charAt(orderbyAsc.length() - 1) != '=') {
                        orderbyAsc += ",";
                    }
                    orderbyAsc += property + "%20asc";
                    urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, id, relationEntityType, orderbyAsc);
                    responseMap = HTTPMethods.doGet(urlString);
                    response = responseMap.get("response").toString();
                    array = new JSONObject(response).getJSONArray("value");
                    for (int i = 1; i < array.length(); i++) {
                        JSONObject obj1 = array.getJSONObject(i - 1);
                        JSONObject obj2 = array.getJSONObject(i);
                        for (int j = 0; j < orderbyPropeties.size(); j++) {
                            String orderbyProperty = orderbyPropeties.get(j);
                            Assert.assertTrue(obj2.get(orderbyProperty).toString().compareTo(obj1.get(orderbyProperty).toString()) >= 0, "The ordering is not correct for EntityType " + entityType + " orderby asc property " + orderbyProperty);
                            if (obj2.get(orderbyProperty).toString().compareTo(obj1.get(orderbyProperty).toString()) != 0) {
                                break;
                            }
                        }
                    }
                    if (orderbyDesc.charAt(orderbyDesc.length() - 1) != '=') {
                        orderbyDesc += ",";
                    }
                    orderbyDesc += property + "%20desc";
                    urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, id, relationEntityType, orderbyDesc);
                    responseMap = HTTPMethods.doGet(urlString);
                    response = responseMap.get("response").toString();
                    array = new JSONObject(response).getJSONArray("value");
                    for (int i = 1; i < array.length(); i++) {
                        JSONObject obj1 = array.getJSONObject(i - 1);
                        JSONObject obj2 = array.getJSONObject(i);
                        for (int j = 0; j < orderbyPropeties.size(); j++) {
                            String orderbyProperty = orderbyPropeties.get(j);
                            Assert.assertTrue(obj1.get(orderbyProperty).toString().compareTo(obj2.get(orderbyProperty).toString()) >= 0, "The ordering is not correct for EntityType " + entityType + " orderby desc property " + orderbyProperty);
                            if (obj2.get(orderbyProperty).toString().compareTo(obj1.get(orderbyProperty).toString()) != 0) {
                                break;
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void checkOrderbyForEntityType(EntityType entityType) {
        String[] properties = EntityProperties.getPropertiesListFor(entityType);
        try {
            //single orderby
            for (String property : properties) {
                if (property.equals("unitOfMeasurement")) {
                    continue;
                }
                String urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$orderby=" + property);
                Map<String, Object> responseMap = HTTPMethods.doGet(urlString);
                String response = responseMap.get("response").toString();
                JSONArray array = new JSONObject(response).getJSONArray("value");
                for (int i = 1; i < array.length(); i++) {
                    JSONObject obj1 = array.getJSONObject(i - 1);
                    JSONObject obj2 = array.getJSONObject(i);
                    Assert.assertTrue(obj2.get(property).toString().compareTo(obj1.get(property).toString()) >= 0, "The ordering is not correct for EntityType " + entityType + " orderby property " + property);
                }
                urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$orderby=" + property + "%20asc");
                responseMap = HTTPMethods.doGet(urlString);
                response = responseMap.get("response").toString();
                array = new JSONObject(response).getJSONArray("value");
                for (int i = 1; i < array.length(); i++) {
                    JSONObject obj1 = array.getJSONObject(i - 1);
                    JSONObject obj2 = array.getJSONObject(i);
                    Assert.assertTrue(obj2.get(property).toString().compareTo(obj1.get(property).toString()) >= 0, "The ordering is not correct for EntityType " + entityType + " orderby asc property " + property);
                }
                urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$orderby=" + property + "%20desc");
                responseMap = HTTPMethods.doGet(urlString);
                response = responseMap.get("response").toString();
                array = new JSONObject(response).getJSONArray("value");
                for (int i = 1; i < array.length(); i++) {
                    JSONObject obj1 = array.getJSONObject(i - 1);
                    JSONObject obj2 = array.getJSONObject(i);
                    Assert.assertTrue(obj1.get(property).toString().compareTo(obj2.get(property).toString()) >= 0, "The ordering is not correct for EntityType " + entityType + " orderby desc property " + property);
                }
            }

            //multiple orderby
            List<String> orderbyPropeties = new ArrayList<>();
            String orderby = "?$orderby=";
            String orderbyAsc = "?$orderby=";
            String orderbyDesc = "?$orderby=";
            for (String property : properties) {
                if (property.equals("unitOfMeasurement")) {
                    continue;
                }
                if (orderby.charAt(orderby.length() - 1) != '=') {
                    orderby += ",";
                }
                orderby += property;
                orderbyPropeties.add(property);
                String urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, orderby);
                Map<String, Object> responseMap = HTTPMethods.doGet(urlString);
                String response = responseMap.get("response").toString();
                JSONArray array = new JSONObject(response).getJSONArray("value");
                for (int i = 1; i < array.length(); i++) {
                    JSONObject obj1 = array.getJSONObject(i - 1);
                    JSONObject obj2 = array.getJSONObject(i);
                    for (int j = 0; j < orderbyPropeties.size(); j++) {
                        String orderbyProperty = orderbyPropeties.get(j);
                        Assert.assertTrue(obj2.get(orderbyProperty).toString().compareTo(obj1.get(orderbyProperty).toString()) >= 0, "The ordering is not correct for EntityType " + entityType + " orderby property " + orderbyProperty);
                        if (obj2.get(orderbyProperty).toString().compareTo(obj1.get(orderbyProperty).toString()) != 0) {
                            break;
                        }
                    }
                }
                if (orderbyAsc.charAt(orderbyAsc.length() - 1) != '=') {
                    orderbyAsc += ",";
                }
                orderbyAsc += property + "%20asc";
                urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, orderbyAsc);
                responseMap = HTTPMethods.doGet(urlString);
                response = responseMap.get("response").toString();
                array = new JSONObject(response).getJSONArray("value");
                for (int i = 1; i < array.length(); i++) {
                    JSONObject obj1 = array.getJSONObject(i - 1);
                    JSONObject obj2 = array.getJSONObject(i);
                    for (int j = 0; j < orderbyPropeties.size(); j++) {
                        String orderbyProperty = orderbyPropeties.get(j);
                        Assert.assertTrue(obj2.get(orderbyProperty).toString().compareTo(obj1.get(orderbyProperty).toString()) >= 0, "The ordering is not correct for EntityType " + entityType + " orderby asc property " + orderbyProperty);
                        if (obj2.get(orderbyProperty).toString().compareTo(obj1.get(orderbyProperty).toString()) != 0) {
                            break;
                        }
                    }
                }
                if (orderbyDesc.charAt(orderbyDesc.length() - 1) != '=') {
                    orderbyDesc += ",";
                }
                orderbyDesc += property + "%20desc";
                urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, orderbyDesc);
                responseMap = HTTPMethods.doGet(urlString);
                response = responseMap.get("response").toString();
                array = new JSONObject(response).getJSONArray("value");
                for (int i = 1; i < array.length(); i++) {
                    JSONObject obj1 = array.getJSONObject(i - 1);
                    JSONObject obj2 = array.getJSONObject(i);
                    for (int j = 0; j < orderbyPropeties.size(); j++) {
                        String orderbyProperty = orderbyPropeties.get(j);
                        Assert.assertTrue(obj1.get(orderbyProperty).toString().compareTo(obj2.get(orderbyProperty).toString()) >= 0, "The ordering is not correct for EntityType " + entityType + " orderby desc property " + orderbyProperty);
                        if (obj2.get(orderbyProperty).toString().compareTo(obj1.get(orderbyProperty).toString()) != 0) {
                            break;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void checkSkipForEntityType(EntityType entityType) {
        try {

            String urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$skip=1");
            Map<String, Object> responseMap = HTTPMethods.doGet(urlString);
            String response = responseMap.get("response").toString();
            JSONArray array = new JSONObject(response).getJSONArray("value");
            try {
                Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response should not have nextLink.");
            } catch (JSONException e) {
            }
            switch (entityType) {
                case THING:
                case LOCATION:
                case FEATURE_OF_INTEREST:
                    Assert.assertEquals(array.length(), 1, "Query requested entities skipping 1, result should have contained 1 entity, but it contains " + array.length());
                    break;
                case OBSERVED_PROPERTY:
                    Assert.assertEquals(array.length(), 2, "Query requested entities skipping 1, result should have contained 2 entities, but it contains " + array.length());
                    break;
                case HISTORICAL_LOCATION:
                case SENSOR:
                case DATASTREAM:
                    Assert.assertEquals(array.length(), 3, "Query requested entities skipping 1, result should have contained 3 entities, but it contains " + array.length());
                    break;
                case OBSERVATION:
                    Assert.assertEquals(array.length(), 11, "Query requested entities skipping 1, result should have contained 11 entities, but it contains " + array.length());
                    break;
                default:
                    break;
            }

            urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$skip=2");
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            try {
                Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response should not have nextLink.");
            } catch (JSONException e) {
            }
            switch (entityType) {
                case THING:
                case LOCATION:
                case FEATURE_OF_INTEREST:
                    Assert.assertEquals(array.length(), 0, "Query requested entities skipping 2, result should have contained 0 entity, but it contains " + array.length());
                    break;
                case OBSERVED_PROPERTY:
                    Assert.assertEquals(array.length(), 1, "Query requested entities skipping 2, result should have contained 1 entity, but it contains " + array.length());
                    break;
                case HISTORICAL_LOCATION:
                case SENSOR:
                case DATASTREAM:
                    Assert.assertEquals(array.length(), 2, "Query requested entities skipping 2, result should have contained 2 entities, but it contains " + array.length());
                    break;
                case OBSERVATION:
                    Assert.assertEquals(array.length(), 10, "Query requested entities skipping 2, result should have contained 10 entities, but it contains " + array.length());
                    break;
                default:
                    break;
            }

            urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$skip=3");
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            try {
                Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response should not have nextLink.");
            } catch (JSONException e) {
            }
            switch (entityType) {
                case THING:
                case LOCATION:
                case FEATURE_OF_INTEREST:
                case OBSERVED_PROPERTY:
                    Assert.assertEquals(array.length(), 0, "Query requested entities skipping 3, result should have contained 0 entity, but it contains " + array.length());
                    break;
                case HISTORICAL_LOCATION:
                case SENSOR:
                case DATASTREAM:
                    Assert.assertEquals(array.length(), 1, "Query requested entities skipping 3, result should have contained 1 entity, but it contains " + array.length());
                    break;
                case OBSERVATION:
                    Assert.assertEquals(array.length(), 9, "Query requested entities skipping 3, result should have contained 9 entities, but it contains " + array.length());
                    break;
                default:
                    break;
            }

            urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$skip=4");
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            try {
                Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response should not have nextLink.");
            } catch (JSONException e) {
            }
            switch (entityType) {
                case THING:
                case LOCATION:
                case FEATURE_OF_INTEREST:
                case OBSERVED_PROPERTY:
                case HISTORICAL_LOCATION:
                case SENSOR:
                case DATASTREAM:
                    Assert.assertEquals(array.length(), 0, "Query requested entities skipping 4, result should have contained 0 entity, but it contains " + array.length());
                    break;
                case OBSERVATION:
                    Assert.assertEquals(array.length(), 8, "Query requested entities skipping 4, result should have contained 8 entities, but it contains " + array.length());
                    break;
                default:
                    break;
            }

            urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$skip=12");
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            try {
                Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response should not have nextLink.");
            } catch (JSONException e) {
            }
            Assert.assertEquals(array.length(), 0, "Query requested entities skipping 12, result should have contained 0 entity, but it contains " + array.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkSkipForEntityTypeRelation(EntityType entityType) {
        try {
            String[] relations = EntityRelations.getRelationsListFor(entityType);
            String urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, null);
            Map<String, Object> responseMap = HTTPMethods.doGet(urlString);
            String response = responseMap.get("response").toString();
            JSONArray array = new JSONObject(response).getJSONArray("value");
            if (array.length() == 0) {
                return;
            }
            long id = array.getJSONObject(0).getLong(ControlInformation.ID);

            for (String relation : relations) {
                if (relation.charAt(relation.length() - 1) != 's' && !relation.equals("FeaturesOfInterest")) {
                    continue;
                }
                EntityType relationEntityType = getEntityTypeFor(relation);
                urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, id, relationEntityType, "?$skip=1");
                responseMap = HTTPMethods.doGet(urlString);
                response = responseMap.get("response").toString();
                array = new JSONObject(response).getJSONArray("value");
                try {
                    Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response should not have nextLink.");
                } catch (JSONException e) {
                }
                switch (entityType) {
                    case THING:
                        switch (relationEntityType) {
                            case LOCATION:
                                Assert.assertEquals(array.length(), 0, "Query requested entities skipping 1, result should have contained 0 entity, but it contains " + array.length());
                                break;
                            case HISTORICAL_LOCATION:
                                Assert.assertEquals(array.length(), 1, "Query requested entities skipping 1, result should have contained 1 entity, but it contains " + array.length());
                                break;
                            case DATASTREAM:
                                Assert.assertEquals(array.length(), 1, "Query requested entities skipping 1, result should have contained 1 entity, but it contains " + array.length());
                                break;
                        }
                        break;
                    case LOCATION:
                        switch (relationEntityType) {
                            case HISTORICAL_LOCATION:
                                Assert.assertEquals(array.length(), 1, "Query requested entities skipping 1, result should have contained 1 entity, but it contains " + array.length());
                                break;
                            case THING:
                                Assert.assertEquals(array.length(), 0, "Query requested entities skipping 1, result should have contained 0 entity, but it contains " + array.length());
                                break;
                        }
                        break;
                    case FEATURE_OF_INTEREST:
                        Assert.assertEquals(array.length(), 5, "Query requested entities skipping 1, result should have contained 5 entities, but it contains " + array.length());
                        break;
                    case OBSERVED_PROPERTY:
                        Assert.assertTrue(array.length() == 1 || array.length() == 0, "Query requested entities skipping 1, result should have contained 0 or 1 entity, but it contains " + array.length());
                        break;
                    case HISTORICAL_LOCATION:
                        switch (relationEntityType) {
                            case LOCATION:
                                Assert.assertEquals(array.length(), 0, "Query requested entities skipping 1, result should have contained 0 entity, but it contains " + array.length());
                                break;
                        }
                        break;
                    case SENSOR:
                        Assert.assertEquals(array.length(), 0, "Query requested entities skipping 1, result should have contained 0 entity, but it contains " + array.length());
                        break;
                    case DATASTREAM:
                        switch (relationEntityType) {
                            case OBSERVATION:
                                Assert.assertEquals(array.length(), 2, "Query requested entities skipping 1, result should have contained 2 entities, but it contains " + array.length());
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkTopForEntityType(EntityType entityType) {
        try {
            String urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$top=1");
            Map<String, Object> responseMap = HTTPMethods.doGet(urlString);
            String response = responseMap.get("response").toString();
            JSONArray array = new JSONObject(response).getJSONArray("value");
            Assert.assertEquals(array.length(), 1, "Query requested 1 entity but response contains " + array.length());
            try {
                Assert.assertNotNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
            } catch (JSONException e) {
                Assert.fail("The response does not have nextLink");
            }

            urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$top=2");
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            Assert.assertEquals(array.length(), 2, "Query requested 2 entities but response contains " + array.length());
            switch (entityType) {
                case THING:
                case LOCATION:
                case FEATURE_OF_INTEREST:
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                default:
                    try {
                        Assert.assertNotNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                        Assert.fail("The response does not have nextLink");
                    }
                    break;
            }

            urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$top=3");
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            switch (entityType) {
                case THING:
                    Assert.assertEquals(array.length(), 2, "Query requested 3 Things, there are only 2 Things,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case LOCATION:
                    Assert.assertEquals(array.length(), 2, "Query requested 3 Locations, there are only 2 Locations,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case FEATURE_OF_INTEREST:
                    Assert.assertEquals(array.length(), 2, "Query requested 3 FeaturesOfInterest, there are only 2 FeaturesOfInterest,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case OBSERVED_PROPERTY:
                    Assert.assertEquals(array.length(), 3, "Query requested 3 entities but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                default:
                    Assert.assertEquals(array.length(), 3, "Query requested 3 entities but response contains " + array.length());
                    try {
                        Assert.assertNotNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                        Assert.fail("The response does not have nextLink");
                    }
                    break;
            }

            urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$top=4");
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            switch (entityType) {
                case THING:
                    Assert.assertEquals(array.length(), 2, "Query requested 4 Things, there are only 2 Things,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case LOCATION:
                    Assert.assertEquals(array.length(), 2, "Query requested 4 Locations, there are only 2 Locations,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case FEATURE_OF_INTEREST:
                    Assert.assertEquals(array.length(), 2, "Query requested 4 FeaturesOfInterest, there are only 2 FeaturesOfInterest,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case OBSERVED_PROPERTY:
                    Assert.assertEquals(array.length(), 3, "Query requested 4 ObservedProperties, there are only 3 ObservedProperties,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case SENSOR:
                case HISTORICAL_LOCATION:
                case DATASTREAM:
                    Assert.assertEquals(array.length(), 4, "Query requested 4 entities but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                default:
                    Assert.assertEquals(array.length(), 4, "Query requested 4 entities but response contains " + array.length());
                    try {
                        Assert.assertNotNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                        Assert.fail("The response does not have nextLink");
                    }
                    break;
            }

            urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$top=5");
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            switch (entityType) {
                case THING:
                    Assert.assertEquals(array.length(), 2, "Query requested 5 Things, there are only 2 Things,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case LOCATION:
                    Assert.assertEquals(array.length(), 2, "Query requested 5 Locations, there are only 2 Locations,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case FEATURE_OF_INTEREST:
                    Assert.assertEquals(array.length(), 2, "Query requested 5 FeaturesOfInterest, there are only 2 FeaturesOfInterest,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case OBSERVED_PROPERTY:
                    Assert.assertEquals(array.length(), 3, "Query requested 5 ObservedProperties, there are only 3 ObservedProperties,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case SENSOR:
                    Assert.assertEquals(array.length(), 4, "Query requested 5 Sensors, there are only 4 Sensors,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case HISTORICAL_LOCATION:
                    Assert.assertEquals(array.length(), 4, "Query requested 5 HistoricalLocations, there are only 4 HistoricalLocations,  but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                case DATASTREAM:
                    Assert.assertEquals(array.length(), 4, "Query requested 5 Datastreams, there are only 4 Datastreams, but response contains " + array.length());
                    try {
                        Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                    }
                    break;
                default:
                    Assert.assertEquals(array.length(), 5, "Query requested 5 entities but response contains " + array.length());
                    try {
                        Assert.assertNotNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
                    } catch (JSONException e) {
                        Assert.fail("The response does not have nextLink");
                    }
                    break;
            }

            urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$top=12");
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            try {
                Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
            } catch (JSONException e) {
            }

            urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, "?$top=13");
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            try {
                Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response does not have nextLink");
            } catch (JSONException e) {
            }
            switch (entityType) {
                case THING:
                    Assert.assertEquals(array.length(), 2, "Query requested 13 Things, there are only 2 Things,  but response contains " + array.length());
                    break;
                case LOCATION:
                    Assert.assertEquals(array.length(), 2, "Query requested 13 Locations, there are only 2 Locations,  but response contains " + array.length());
                    break;
                case FEATURE_OF_INTEREST:
                    Assert.assertEquals(array.length(), 2, "Query requested 13 FeaturesOfInterest, there are only 2 FeaturesOfInterest,  but response contains " + array.length());
                    break;
                case OBSERVED_PROPERTY:
                    Assert.assertEquals(array.length(), 3, "Query requested 13 ObservedProperties, there are only 3 ObservedProperties,  but response contains " + array.length());
                    break;
                case SENSOR:
                    Assert.assertEquals(array.length(), 4, "Query requested 13 Sensors, there are only 4 Sensors,  but response contains " + array.length());
                    break;
                case HISTORICAL_LOCATION:
                    Assert.assertEquals(array.length(), 4, "Query requested 13 HistoricalLocations, there are only 4 HistoricalLocations,  but response contains " + array.length());
                    break;
                case DATASTREAM:
                    Assert.assertEquals(array.length(), 4, "Query requested 13 Datastreams, there are only 4 Datastreams, but response contains " + array.length());
                    break;
                case OBSERVATION:
                    Assert.assertEquals(array.length(), 12, "Query requested 13 Observations, there are only 12 Observations, but response contains " + array.length());
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkTopForEntityTypeRelation(EntityType entityType) {
        try {
            String[] relations = EntityRelations.getRelationsListFor(entityType);
            String urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, null);
            Map<String, Object> responseMap = HTTPMethods.doGet(urlString);
            String response = responseMap.get("response").toString();
            JSONArray array = new JSONObject(response).getJSONArray("value");
            if (array.length() == 0) {
                return;
            }
            long id = array.getJSONObject(0).getLong(ControlInformation.ID);

            for (String relation : relations) {
                if (relation.charAt(relation.length() - 1) != 's' && !relation.equals("FeaturesOfInterest")) {
                    continue;
                }
                EntityType relationEntityType = getEntityTypeFor(relation);
                urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, id, relationEntityType, "?$skip=1");
                responseMap = HTTPMethods.doGet(urlString);
                response = responseMap.get("response").toString();
                array = new JSONObject(response).getJSONArray("value");
                try {
                    Assert.assertNull(new JSONObject(response).get("@iot.nextLink"), "The response should not have nextLink.");
                } catch (JSONException e) {
                }
                switch (entityType) {
                    case THING:
                        switch (relationEntityType) {
                            case LOCATION:
                                Assert.assertEquals(array.length(), 0, "Query requested entities skipping 1, result should have contained 0 entity, but it contains " + array.length());
                                break;
                            case HISTORICAL_LOCATION:
                                Assert.assertEquals(array.length(), 1, "Query requested entities skipping 1, result should have contained 1 entity, but it contains " + array.length());
                                break;
                            case DATASTREAM:
                                Assert.assertEquals(array.length(), 1, "Query requested entities skipping 1, result should have contained 1 entity, but it contains " + array.length());
                                break;
                        }
                        break;
                    case LOCATION:
                        switch (relationEntityType) {
                            case HISTORICAL_LOCATION:
                                Assert.assertEquals(array.length(), 1, "Query requested entities skipping 1, result should have contained 1 entity, but it contains " + array.length());
                                break;
                            case THING:
                                Assert.assertEquals(array.length(), 0, "Query requested entities skipping 1, result should have contained 0 entity, but it contains " + array.length());
                                break;
                        }
                        break;
                    case FEATURE_OF_INTEREST:
                        Assert.assertEquals(array.length(), 5, "Query requested entities skipping 1, result should have contained 5 entities, but it contains " + array.length());
                        break;
                    case OBSERVED_PROPERTY:
                        Assert.assertTrue(array.length() == 1 || array.length() == 0, "Query requested entities skipping 1, result should have contained 0 or 1 entity, but it contains " + array.length());
                        break;
                    case HISTORICAL_LOCATION:
                        switch (relationEntityType) {
                            case LOCATION:
                                Assert.assertEquals(array.length(), 0, "Query requested entities skipping 1, result should have contained 0 entity, but it contains " + array.length());
                                break;
                        }
                        break;
                    case SENSOR:
                        Assert.assertEquals(array.length(), 0, "Query requested entities skipping 1, result should have contained 0 entity, but it contains " + array.length());
                        break;
                    case DATASTREAM:
                        switch (relationEntityType) {
                            case OBSERVATION:
                                Assert.assertEquals(array.length(), 2, "Query requested entities skipping 1, result should have contained 2 entities, but it contains " + array.length());
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkSelectForEntityType(EntityType entityType) {
        List<String> selectedProperties;
        String[] properties = EntityProperties.getPropertiesListFor(entityType);
        for (String property : properties) {
            selectedProperties = new ArrayList<>();
            selectedProperties.add(property);
            String response = getEntities(entityType, selectedProperties, null);
            checkEntitiesAllAspectsForSelectResponse(entityType, response, selectedProperties);
        }
        selectedProperties = new ArrayList<>();
        for (String property : properties) {
            selectedProperties.add(property);
            String response = getEntities(entityType, selectedProperties, null);
            checkEntitiesAllAspectsForSelectResponse(entityType, response, selectedProperties);
        }
    }

    public String getEntities(EntityType entityType, List<String> selectedProperties, List<String> expandedRelations) {
        String urlString = rootUri;
        String selectString = "";
        if (selectedProperties != null && selectedProperties.size() > 0) {
            selectString = "?$select=";
            for (String select : selectedProperties) {
                if (selectString.charAt(selectString.length() - 1) != '=') {
                    selectString += ',';
                }
                selectString += select;
            }
        }
        String expandString = "";
        if (expandedRelations != null && expandedRelations.size() > 0) {
            expandString = selectString.equals("") ? "?$expand=" : "&$expand=";
            for (String expand : expandedRelations) {
                if (expandString.charAt(expandString.length() - 1) != '=') {
                    expandString += ',';
                }
                expandString += expand;
            }
        }
        if (entityType != null) {
            urlString = ServiceURLBuilder.buildURLString(rootUri, entityType, -1, null, selectString + expandString);
        }
        Map<String, Object> responseMap = HTTPMethods.doGet(urlString);
        String response = responseMap.get("response").toString();
        int responseCode = Integer.parseInt(responseMap.get("response-code").toString());
        Assert.assertEquals(responseCode, 200, "Error during getting entities: " + entityType.name());
        if (entityType != null) {
            Assert.assertTrue(response.indexOf("value") != -1, "The GET entities response for entity type \"" + entityType + "\" does not match SensorThings API : missing \"value\" in response.");
        } else { // GET Service Base URI
            Assert.assertTrue(response.indexOf("value") != -1, "The GET entities response for service root URI does not match SensorThings API : missing \"value\" in response.");
        }
        return response;
    }

    public void checkEntitiesAllAspectsForSelectResponse(EntityType entityType, String response, List<String> selectedProperties) {
        checkEntitiesProperties(entityType, response, selectedProperties);
        checkEntitiesRelations(entityType, response, selectedProperties, null);
    }


    public void checkEntitiesProperties(EntityType entityType, String response, List<String> selectedProperties) {
        try {
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray entities = jsonResponse.getJSONArray("value");
            checkPropertiesForEntityArray(entityType, entities, selectedProperties);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void checkPropertiesForEntityArray(EntityType entityType, JSONArray entities, List<String> selectedProperties) {
        int count = 0;
        for (int i = 0; i < entities.length() && count < 2; i++) {
            count++;
            JSONObject entity = null;
            try {
                entity = entities.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            checkEntityProperties(entityType, entity, selectedProperties);
        }
    }

    public void checkEntityProperties(EntityType entityType, Object response, List<String> selectedProperties) {
        try {
            JSONObject entity = new JSONObject(response.toString());
            String[] properties = EntityProperties.getPropertiesListFor(entityType);
            for (String property : properties) {
                if (selectedProperties.contains(property)) {
                    try {
                        Assert.assertNotNull(entity.get(property), "Entity type \"" + entityType + "\" does not have selected property: \"" + property + "\".");
                    } catch (JSONException e) {
                        Assert.fail("Entity type \"" + entityType + "\" does not have selected property: \"" + property + "\".");
                    }
                } else {
                    try {
                        Assert.assertNull(entity.get(property), "Entity type \"" + entityType + "\" contains not-selected property: \"" + property + "\".");
                    } catch (JSONException e) {
                    }
                }
            }
        } catch (JSONException e) {
            //The program reachs here in normal state, because it tries to check the non-existense of some navigation properties.
        }

    }

    public void checkEntitiesRelations(EntityType entityType, String response, List<String> selectedProperties, List<String> expandedRelations) {
        try {
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray entities = jsonResponse.getJSONArray("value");
            int count = 0;
            for (int i = 0; i < entities.length() && count < 2; i++) {
                count++;
                JSONObject entity = entities.getJSONObject(i);
                checkEntityRelations(entityType, entity, selectedProperties, expandedRelations);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void checkEntityRelations(EntityType entityType, Object response, List<String> selectedProperties, List<String> expandedRelations) {
        try {
            JSONObject entity = new JSONObject(response.toString());
            String[] relations = EntityRelations.getRelationsListFor(entityType);
            for (String relation : relations) {
                if (selectedProperties == null || selectedProperties.contains(relation)) {
                    if (expandedRelations == null || !expandedRelations.contains(relation)) {
                        try {
                            Assert.assertNotNull(entity.get(relation + ControlInformation.NAVIGATION_LINK), "Entity type \"" + entityType + "\" does not have selected relation: \"" + relation + "\".");
                        } catch (JSONException e) {
                            Assert.fail("Entity type \"" + entityType + "\" does not have selected relation: \"" + relation + "\".");
                        }
                    } else {
                        try {
                            Assert.assertNotNull(entity.get(relation), "Entity type \"" + entityType + "\" does not have expanded relation Correctly: \"" + relation + "\".");
                            JSONArray expandedEntityArray = null;
                            if(relation.charAt(relation.length()-1)!='s' && !relation.equals("FeaturesOfInterest")){
                                expandedEntityArray = new JSONArray();
                                expandedEntityArray.put(entity.getJSONObject(relation));
                            } else{
                                expandedEntityArray = entity.getJSONArray(relation);
                            }
                            checkPropertiesForEntityArray(getEntityTypeFor(relation), expandedEntityArray, new ArrayList<String>(Arrays.asList(EntityProperties.getPropertiesListFor(relation))));
                        } catch (JSONException e) {
                            Assert.fail("Entity type \"" + entityType + "\" does not have expanded relation Correctly: \"" + relation + "\".");
                        }
                    }
                } else {
                    try {
                        Assert.assertNull(entity.get(relation + ControlInformation.NAVIGATION_LINK), "Entity type \"" + entityType + "\" contains not-selectd relation: \"" + relation + "\".");
                    } catch (JSONException e) {
                    }
                    try {
                        Assert.assertNull(entity.get(relation), "Entity type \"" + entityType + "\" contains not-selectd relation: \"" + relation + "\".");
                    } catch (JSONException e) {
                    }
                }
            }
        } catch (JSONException e) {
            //The program reachs here in normal state, because it tries to check the non-existense of some navigation properties.
        }
    }

    private void checkExpandtForEntityType(EntityType entityType) {
        List<String> expandedRelations;
        String[] relations = EntityRelations.getRelationsListFor(entityType);
        for (String relation : relations) {
            expandedRelations = new ArrayList<>();
            expandedRelations.add(relation);
            String response = getEntities(entityType, null, expandedRelations);
            checkEntitiesAllAspectsForExpandResponse(entityType, response, expandedRelations);
        }
        expandedRelations = new ArrayList<>();
        for (String relation : relations) {
            expandedRelations.add(relation);
            String response = getEntities(entityType, null, expandedRelations);
            checkEntitiesAllAspectsForExpandResponse(entityType, response, expandedRelations);
        }
    }

    public void checkEntitiesAllAspectsForExpandResponse(EntityType entityType, String response, List<String> expandedRelations) {
        checkEntitiesRelations(entityType, response, null, expandedRelations);
    }

    private EntityType getEntityTypeFor(String name) {
        switch (name.toLowerCase()) {
            case "thing":
            case "things":
                return EntityType.THING;
            case "location":
            case "locations":
                return EntityType.LOCATION;
            case "historicallocation":
            case "historicallocations":
                return EntityType.HISTORICAL_LOCATION;
            case "datastream":
            case "datastreams":
                return EntityType.DATASTREAM;
            case "sensor":
            case "sensors":
                return EntityType.SENSOR;
            case "observedproperty":
            case "observedproperties":
                return EntityType.OBSERVED_PROPERTY;
            case "observation":
            case "observations":
                return EntityType.OBSERVATION;
            case "featureofinterest":
            case "featuresofinterest":
                return EntityType.FEATURE_OF_INTEREST;
        }
        return null;
    }




    private void createEntities(){
        try {
            //First Thing
            String urlParameters = "{\n" +
                    "    \"description\": \"thing 1\",\n" +
                    "    \"properties\": {\n" +
                    "        \"reference\": \"first\"\n" +
                    "    },\n" +
                    "    \"Locations\": [\n" +
                    "        {\n" +
                    "            \"description\": \"location 1\",\n" +
                    "            \"location\": {\n" +
                    "                \"type\": \"Point\",\n" +
                    "                \"coordinates\": [\n" +
                    "                    -117.05,\n" +
                    "                    51.05\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"encodingType\": \"http://example.org/location_types#GeoJSON\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"Datastreams\": [\n" +
                    "        {\n" +
                    "            \"unitOfMeasurement\": {\n" +
                    "                \"name\": \"Lumen\",\n" +
                    "                \"symbol\": \"lm\",\n" +
                    "                \"definition\": \"http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#Lumen\"\n" +
                    "            },\n" +
                    "            \"description\": \"datastream 1\",\n" +
                    "            \"observationType\": \"http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement\",\n" +
                    "            \"ObservedProperty\": {\n" +
                    "                \"name\": \"Luminous Flux\",\n" +
                    "                \"definition\": \"http://www.qudt.org/qudt/owl/1.0.0/quantity/Instances.html#LuminousFlux\",\n" +
                    "                \"description\": \"observedProperty 1\"\n" +
                    "            },\n" +
                    "            \"Sensor\": {\n" +
                    "                \"description\": \"sensor 1\",\n" +
                    "                \"encodingType\": \"http://schema.org/description\",\n" +
                    "                \"metadata\": \"Light flux sensor\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"unitOfMeasurement\": {\n" +
                    "                \"name\": \"Centigrade\",\n" +
                    "                \"symbol\": \"C\",\n" +
                    "                \"definition\": \"http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#Lumen\"\n" +
                    "            },\n" +
                    "            \"description\": \"datastream 2\",\n" +
                    "            \"observationType\": \"http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement\",\n" +
                    "            \"ObservedProperty\": {\n" +
                    "                \"name\": \"Tempretaure\",\n" +
                    "                \"definition\": \"http://www.qudt.org/qudt/owl/1.0.0/quantity/Instances.html#Tempreture\",\n" +
                    "                \"description\": \"observedProperty 2\"\n" +
                    "            },\n" +
                    "            \"Sensor\": {\n" +
                    "                \"description\": \"sensor 2\",\n" +
                    "                \"encodingType\": \"http://schema.org/description\",\n" +
                    "                \"metadata\": \"Tempreture sensor\"\n" +
                    "            }\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
            String urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.THING, -1, null, null);
            Map<String, Object> responseMap = HTTPMethods.doPost(urlString, urlParameters);
            String response = responseMap.get("response").toString();
            thingId1 = Long.parseLong(response.substring(response.indexOf("(") + 1, response.indexOf(")")));

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.THING, thingId1, EntityType.LOCATION, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            JSONArray array = new JSONObject(response).getJSONArray("value");
            locationId1 = array.getJSONObject(0).getLong(ControlInformation.ID);

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.THING, thingId1, EntityType.DATASTREAM, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            datastreamId1 = array.getJSONObject(0).getLong(ControlInformation.ID);
            datastreamId2 = array.getJSONObject(1).getLong(ControlInformation.ID);

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.DATASTREAM, datastreamId1, EntityType.SENSOR, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            sensorId1 = new JSONObject(response).getLong(ControlInformation.ID);
            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.DATASTREAM, datastreamId1, EntityType.OBSERVED_PROPERTY, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            observedPropertyId1 = new JSONObject(response).getLong(ControlInformation.ID);

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.DATASTREAM, datastreamId2, EntityType.SENSOR, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            sensorId2 = new JSONObject(response).getLong(ControlInformation.ID);
            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.DATASTREAM, datastreamId1, EntityType.OBSERVED_PROPERTY, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            observedPropertyId2 = new JSONObject(response).getLong(ControlInformation.ID);


            //Second Thing
            urlParameters = "{\n" +
                    "    \"description\": \"thing 2\",\n" +
                    "    \"properties\": {\n" +
                    "        \"reference\": \"second\"\n" +
                    "    },\n" +
                    "    \"Locations\": [\n" +
                    "        {\n" +
                    "            \"description\": \"location 2\",\n" +
                    "            \"location\": {\n" +
                    "                \"type\": \"Point\",\n" +
                    "                \"coordinates\": [\n" +
                    "                    -100.05,\n" +
                    "                    50.05\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"encodingType\": \"http://example.org/location_types#GeoJSON\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"Datastreams\": [\n" +
                    "        {\n" +
                    "            \"unitOfMeasurement\": {\n" +
                    "                \"name\": \"Lumen\",\n" +
                    "                \"symbol\": \"lm\",\n" +
                    "                \"definition\": \"http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#Lumen\"\n" +
                    "            },\n" +
                    "            \"description\": \"datastream 3\",\n" +
                    "            \"observationType\": \"http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement\",\n" +
                    "            \"ObservedProperty\": {\n" +
                    "                \"name\": \"Second Luminous Flux\",\n" +
                    "                \"definition\": \"http://www.qudt.org/qudt/owl/1.0.0/quantity/Instances.html#LuminousFlux\",\n" +
                    "                \"description\": \"observedProperty 3\"\n" +
                    "            },\n" +
                    "            \"Sensor\": {\n" +
                    "                \"description\": \"sensor 3\",\n" +
                    "                \"encodingType\": \"http://schema.org/description\",\n" +
                    "                \"metadata\": \"Second Light flux sensor\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"unitOfMeasurement\": {\n" +
                    "                \"name\": \"Centigrade\",\n" +
                    "                \"symbol\": \"C\",\n" +
                    "                \"definition\": \"http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html#Lumen\"\n" +
                    "            },\n" +
                    "            \"description\": \"datastream 2\",\n" +
                    "            \"observationType\": \"http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement\",\n" +
                    "            \"ObservedProperty\": {\n" +
                    "                \"@iot.id\": "+observedPropertyId2+"\n" +
                    "            },\n" +
                    "            \"Sensor\": {\n" +
                    "                \"description\": \"sensor 4 \",\n" +
                    "                \"encodingType\": \"http://schema.org/description\",\n" +
                    "                \"metadata\": \"Second Tempreture sensor\"\n" +
                    "            }\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.THING, -1, null, null);
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            thingId2 = Long.parseLong(response.substring(response.indexOf("(") + 1, response.indexOf(")")));

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.THING, thingId2, EntityType.LOCATION, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            locationId2 = array.getJSONObject(0).getLong(ControlInformation.ID);

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.THING, thingId2, EntityType.DATASTREAM, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            datastreamId3 = array.getJSONObject(0).getLong(ControlInformation.ID);
            datastreamId4 = array.getJSONObject(1).getLong(ControlInformation.ID);

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.DATASTREAM, datastreamId3, EntityType.SENSOR, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            sensorId3 = new JSONObject(response).getLong(ControlInformation.ID);
            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.DATASTREAM, datastreamId3, EntityType.OBSERVED_PROPERTY, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            observedPropertyId3 = new JSONObject(response).getLong(ControlInformation.ID);

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.DATASTREAM, datastreamId4, EntityType.SENSOR, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            sensorId4 = new JSONObject(response).getLong(ControlInformation.ID);

            //HistoricalLocations
            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.THING, thingId1, null, null);
            urlParameters = "{\"Locations\": [\n" +
                    "    {\n" +
                    "      \"@iot.id\": "+locationId2+"\n" +
                    "    }\n" +
                    "  ]}";
            HTTPMethods.doPatch(urlString, urlParameters);

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.THING, thingId2, null, null);
            urlParameters = "{\"Locations\": [\n" +
                    "    {\n" +
                    "      \"@iot.id\": "+locationId1+"\n" +
                    "    }\n" +
                    "  ]}";
            HTTPMethods.doPatch(urlString, urlParameters);

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.THING, thingId1, EntityType.HISTORICAL_LOCATION, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            historicalLocationId1 = array.getJSONObject(0).getLong(ControlInformation.ID);
            historicalLocationId2 = array.getJSONObject(1).getLong(ControlInformation.ID);

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.THING, thingId2, EntityType.HISTORICAL_LOCATION, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            array = new JSONObject(response).getJSONArray("value");
            historicalLocationId3 = array.getJSONObject(0).getLong(ControlInformation.ID);
            historicalLocationId4 = array.getJSONObject(1).getLong(ControlInformation.ID);

            //Observations
            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.DATASTREAM, datastreamId1, EntityType.OBSERVATION, null);
            urlParameters = "{\n" +
                    "  \"phenomenonTime\": \"2015-03-01T00:00:00Z\",\n" +
                    "  \"result\": 1 \n" +
                    "   }";
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            observationId1 = Long.parseLong(response.substring(response.lastIndexOf("(") + 1, response.lastIndexOf(")")));
            urlParameters = "{\n" +
                    "  \"phenomenonTime\": \"2015-03-02T00:00:00Z\",\n" +
                    "  \"result\": 2 \n" +
                    "   }";
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            observationId2 = Long.parseLong(response.substring(response.lastIndexOf("(") + 1, response.lastIndexOf(")")));
            urlParameters = "{\n" +
                    "  \"phenomenonTime\": \"2015-03-03T00:00:00Z\",\n" +
                    "  \"result\": 3 \n" +
                    "   }";
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            observationId3 = Long.parseLong(response.substring(response.lastIndexOf("(") + 1, response.lastIndexOf(")")));

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.DATASTREAM, datastreamId2, EntityType.OBSERVATION, null);
            urlParameters = "{\n" +
                    "  \"phenomenonTime\": \"2015-03-04T00:00:00Z\",\n" +
                    "  \"result\": 4 \n" +
                    "   }";
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            observationId4 = Long.parseLong(response.substring(response.lastIndexOf("(") + 1, response.lastIndexOf(")")));
            urlParameters = "{\n" +
                    "  \"phenomenonTime\": \"2015-03-05T00:00:00Z\",\n" +
                    "  \"result\": 5 \n" +
                    "   }";
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            observationId5 = Long.parseLong(response.substring(response.lastIndexOf("(") + 1, response.lastIndexOf(")")));
            urlParameters = "{\n" +
                    "  \"phenomenonTime\": \"2015-03-06T00:00:00Z\",\n" +
                    "  \"result\": 6 \n" +
                    "   }";
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            observationId6 = Long.parseLong(response.substring(response.lastIndexOf("(") + 1, response.lastIndexOf(")")));

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.DATASTREAM, datastreamId3, EntityType.OBSERVATION, null);
            urlParameters = "{\n" +
                    "  \"phenomenonTime\": \"2015-03-07T00:00:00Z\",\n" +
                    "  \"result\": 7 \n" +
                    "   }";
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            observationId7 = Long.parseLong(response.substring(response.lastIndexOf("(") + 1, response.lastIndexOf(")")));
            urlParameters = "{\n" +
                    "  \"phenomenonTime\": \"2015-03-08T00:00:00Z\",\n" +
                    "  \"result\": 8 \n" +
                    "   }";
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            observationId8 = Long.parseLong(response.substring(response.lastIndexOf("(") + 1, response.lastIndexOf(")")));
            urlParameters = "{\n" +
                    "  \"phenomenonTime\": \"2015-03-09T00:00:00Z\",\n" +
                    "  \"result\": 9 \n" +
                    "   }";
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            observationId9 = Long.parseLong(response.substring(response.lastIndexOf("(") + 1, response.lastIndexOf(")")));

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.DATASTREAM, datastreamId4, EntityType.OBSERVATION, null);
            urlParameters = "{\n" +
                    "  \"phenomenonTime\": \"2015-03-10T00:00:00Z\",\n" +
                    "  \"result\": 10 \n" +
                    "   }";
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            observationId10 = Long.parseLong(response.substring(response.lastIndexOf("(") + 1, response.lastIndexOf(")")));
            urlParameters = "{\n" +
                    "  \"phenomenonTime\": \"2015-03-11T00:00:00Z\",\n" +
                    "  \"result\": 11 \n" +
                    "   }";
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            observationId11 = Long.parseLong(response.substring(response.lastIndexOf("(") + 1, response.lastIndexOf(")")));
            urlParameters = "{\n" +
                    "  \"phenomenonTime\": \"2015-03-12T00:00:00Z\",\n" +
                    "  \"result\": 12 \n" +
                    "   }";
            responseMap = HTTPMethods.doPost(urlString, urlParameters);
            response = responseMap.get("response").toString();
            observationId12 = Long.parseLong(response.substring(response.lastIndexOf("(") + 1, response.lastIndexOf(")")));

            //FeatureOfInterest
            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.OBSERVATION, observationId1, EntityType.FEATURE_OF_INTEREST, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            featureOfInterestId1 = new JSONObject(response).getLong(ControlInformation.ID);

            urlString = ServiceURLBuilder.buildURLString(rootUri, EntityType.OBSERVATION, observationId7, EntityType.FEATURE_OF_INTEREST, null);
            responseMap = HTTPMethods.doGet(urlString);
            response = responseMap.get("response").toString();
            featureOfInterestId2 = new JSONObject(response).getLong(ControlInformation.ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
