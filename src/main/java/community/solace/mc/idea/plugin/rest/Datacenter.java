package community.solace.mc.idea.plugin.rest;

import java.util.Arrays;

public enum Datacenter {
    AKS_AUSTRALIAEAST("aks-australiaeast", "Australia East (New South Wales)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    AKS_CANADACENTRAL("aks-canadacentral", "Canada Central (Toronto)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    AKS_CANADAEAST("aks-canadaeast", "Canada East (Quebec City)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    AKS_CENTRALINDIA("aks-centralindia", "Central India (Pune)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    AKS_CENTRALUS("aks-centralus", "Central US (Iowa)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    AKS_EASTASIA("aks-eastasia", "East Asia (Hong Kong)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    AKS_EASTUS2("aks-eastus2", "East US 2 (Virginia)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    AKS_FRANCECENTRAL("aks-francecentral", "France Central (Paris)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    AKS_GERMANYNORTH("aks-germanynorth", "Germany North (Berlin)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    AKS_SOUTHEASTASIA("aks-southeastasia", "SouthEast Asia (Singapore)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    AKS_UKSOUTH("aks-uksouth", "UK South (London)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    AKS_WESTEUROPE("aks-westeurope", "West Europe (Netherlands)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    AKS_WESTUS2("aks-westus2", "West US 2 (Washington)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_AF_S("eks-af-south-1b", "Africa (Cape Town)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_AP_NE("eks-ap-northeast-1a", "Asia Pacific (Tokyo)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_AP_S("eks-ap-south-1a", "Asia Pacific South (Mumbai)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_AP_SE_1("eks-ap-southeast-1a", "Asia Pacific (Singapore)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_AP_SE_2("eks-ap-southeast-2a", "Asia Pacific (Sydney)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_AP_SE_3("eks-ap-southeast-3a", "Asia Pacific (Jakarta)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_CA("eks-ca-central-1a", "Canada Central(Montreal)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_EU_CENTRAL("eks-eu-central-1a", "EU (Frankfurt)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_EU_W_1("eks-eu-west-1a", "EU (Ireland)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_EU_W_2("eks-eu-west-2a", "EU (London)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_SA_E("eks-sa-east-1a", "South America (Sao Paulo)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_US_E_1("eks-us-east-1a", "US East (N. Virginia)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_US_E_2("eks-us-east-2a", "US East (Ohio)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_US_W_1("eks-us-west-1a", "US West (N. California)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    EKS_US_W_2("eks-us-west-2a", "US West (Oregon)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    GKE_ASIA_NE("gke-gcp-asia-northeast1-a", "Asia NE (Tokyo)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    GKE_ASIA_S("gke-gcp-asia-south1-a", "Asia South (Mumbai)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    GKE_ASIA_SE("gke-gcp-asia-southeast1-a", "Asia SE (Singapore)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    GKE_AUS_SE("gke-gcp-australia-southeast1-a", "Southeast (Sydney)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    GKE_EU_W_1("gke-gcp-europe-west1-b", "EU West (Belgium)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    GKE_EU_W_2("gke-gcp-europe-west2-a", "EU (London)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    GKE_EU_W_3("gke-gcp-europe-west3-a", "EU (Frankfurt)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    GKE_EU_W_6("gke-gcp-europe-west6|E", "EU West (Zurich)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    GKE_US_CENTRAL("gke-gcp-us-central1-a", "US Central (Iowa)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    GKE_US_E("gke-gcp-us-east4-a", "US East 4 (N. Virginia)", new String[]{"Developer","Enterprise 10K Standalone","Enterprise 250 Standalone","Enterprise 250 HA","Enterprise 1K HA","Enterprise 100K Standalone","Enterprise 10K HA","Enterprise 50K Standalone","Enterprise 5K HA","Enterprise 1K Standalone","Enterprise 100K HA","Enterprise 5K Standalone"}),
    ;

    private final String id;
    private final String name;
    private final String[] classes;

    Datacenter(String id, String name, String[] classes) {
        this.id = id;
        this.name = name;
        this.classes = classes;

        Arrays.sort(this.classes, (str1, str2) -> {
            if (str1.equals("Developer")) {
                return -1;
            } else if (str2.equals("Developer")) {
                return 1;
            } else if (str1.contains("Standalone") && str2.contains("HA")) {
                return -1;
            } else if (str2.contains("Standalone") && str1.contains("HA")) {
                return 1;
            } else if (str1.contains("250") && str2.contains("K")) {
                return -1;
            } else if (str2.contains("250") && str1.contains("K")) {
                return 1;
            } else if (str1.contains("K") && str2.contains("K")) {
                return Integer.parseInt(str1.split(" ")[1].replace("K", "")) -
                Integer.parseInt(str2.split(" ")[1].replace("K", ""));
            }

            return 0;
        });
    }

    public String getId() {
        return id;
    }

    public String[] getServiceClasses() {
        return classes;
    }

    @Override
    public String toString() {
        return name;
    }
}
