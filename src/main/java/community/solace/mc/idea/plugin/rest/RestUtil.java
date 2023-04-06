package community.solace.mc.idea.plugin.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.util.LinkedHashMap;
import java.util.Map;

import static community.solace.mc.idea.plugin.rest.Datacenter.*;

public class RestUtil {
    // Temporary until Mission Control v2 REST APIs for datacenters and service classes are released
    public static final Map<String, String> SERVICE_CLASSES = new LinkedHashMap<>();
    public static final Map<String, String> REVERSE_SERVICE_CLASSES = new LinkedHashMap<>();
    public static final Map<String, Datacenter[]> DATACENTERS = Map.of(
        "azure", new Datacenter[]{
            AKS_AUSTRALIAEAST,
            AKS_CANADACENTRAL,
            AKS_CANADAEAST,
            AKS_CENTRALINDIA,
            AKS_CENTRALUS,
            AKS_EASTASIA,
            AKS_EASTUS2,
            AKS_FRANCECENTRAL,
            AKS_GERMANYNORTH,
            AKS_SOUTHEASTASIA,
            AKS_UKSOUTH,
            AKS_WESTEUROPE,
            AKS_WESTUS2
        },
        "aws", new Datacenter[]{
            EKS_AF_S,
            EKS_AP_NE,
            EKS_AP_S,
            EKS_AP_SE_1,
            EKS_AP_SE_2,
            EKS_AP_SE_3,
            EKS_CA,
            EKS_EU_CENTRAL,
            EKS_EU_W_1,
            EKS_EU_W_2,
            EKS_SA_E,
            EKS_US_E_1,
            EKS_US_E_2,
            EKS_US_W_1,
            EKS_US_W_2
        },
        "gcp", new Datacenter[]{
            GKE_ASIA_NE,
            GKE_ASIA_S,
            GKE_ASIA_SE,
            GKE_AUS_SE,
            GKE_EU_W_1,
            GKE_EU_W_2,
            GKE_EU_W_3,
            GKE_EU_W_6,
            GKE_US_CENTRAL,
            GKE_US_E
        }
    );

    static {
        // Map.of does not preserve order
        SERVICE_CLASSES.put("DEVELOPER", "Developer");
        SERVICE_CLASSES.put("ENTERPRISE_250_STANDALONE", "Enterprise 250 Standalone");
        SERVICE_CLASSES.put("ENTERPRISE_1K_STANDALONE", "Enterprise 1K Standalone");
        SERVICE_CLASSES.put("ENTERPRISE_5K_STANDALONE", "Enterprise 5K Standalone");
        SERVICE_CLASSES.put("ENTERPRISE_10K_STANDALONE", "Enterprise 10K Standalone");
        SERVICE_CLASSES.put("ENTERPRISE_50K_STANDALONE", "Enterprise 50K Standalone");
        SERVICE_CLASSES.put("ENTERPRISE_100K_STANDALONE", "Enterprise 100K Standalone");
        SERVICE_CLASSES.put("ENTERPRISE_250_HIGHAVAILABILITY", "Enterprise 250 HA");
        SERVICE_CLASSES.put("ENTERPRISE_1K_HIGHAVAILABILITY", "Enterprise 1K HA");
        SERVICE_CLASSES.put("ENTERPRISE_5K_HIGHAVAILABILITY", "Enterprise 5K HA");
        SERVICE_CLASSES.put("ENTERPRISE_10K_HIGHAVAILABILITY", "Enterprise 10K HA");
        SERVICE_CLASSES.put("ENTERPRISE_50K_HIGHAVAILABILITY", "Enterprise 50K HA");
        SERVICE_CLASSES.put("ENTERPRISE_100K_HIGHAVAILABILITY", "Enterprise 100K HA");

        // Reverse lookup from service creation combo-box which uses the name, not the ID
        for (Map.Entry<String, String> kv : SERVICE_CLASSES.entrySet()) {
            REVERSE_SERVICE_CLASSES.put(kv.getValue(), kv.getKey());
        }
    }
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String getMessage(String responseBody) {
        Map<String, String> result = GSON.fromJson(responseBody, Map.class);
        return result.get("message");
    }

    public static String prettyPrint(String jsonString) {
        JsonElement je = JsonParser.parseString(jsonString);
        try {
            return GSON.toJson(je);
        } catch (JsonParseException e) {
            return jsonString;
        }
    }
}
