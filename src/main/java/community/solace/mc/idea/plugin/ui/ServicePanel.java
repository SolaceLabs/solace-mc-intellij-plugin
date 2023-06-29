package community.solace.mc.idea.plugin.ui;

import com.solace.mc.api.EventBrokerServicesApi;
import com.solace.mc.invoker.ApiException;
import com.solace.mc.model.ConnectionEndpoint;
import com.solace.mc.model.GetService;
import com.solace.mc.model.GetServiceResponse;
import com.solace.mc.model.ServiceConnectionEndpointPort;
import community.solace.mc.idea.plugin.rest.MissionControlCallback;
import community.solace.mc.idea.plugin.ui.service.InfoTab;
import community.solace.mc.idea.plugin.ui.service.QueueTab;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServicePanel extends JTabbedPane {
    private static final Set<String> EXPAND = Set.of("broker", "serviceConnectionEndpoints");

    InfoTab infoTab = new InfoTab();
    QueueTab queueTab = new QueueTab();
    TryMePanel tryMePanel;

    public ServicePanel(EventBrokerServicesApi api, String id, TryMePanel tryMePanel) {
        this.tryMePanel = tryMePanel;

        try {
            // Get endpoint details
            api.getServiceAsync(id, EXPAND, new GetServiceCallback());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        addTab("Info", infoTab);
        addTab("Queues", queueTab);
    }

    private class GetServiceCallback implements MissionControlCallback<GetServiceResponse> {
        @Override
        public void onSuccess(GetServiceResponse result, int statusCode, Map<String, List<String>> responseHeaders) {
            GetService response = result.getData();

            String vpnName = response.getBroker().getMsgVpns().get(0).getMsgVpnName();
            String username = response.getBroker().getMsgVpns().get(0).getServiceLoginCredential().getUsername();
            String password = response.getBroker().getMsgVpns().get(0).getServiceLoginCredential().getPassword();
            int mgmtPort = 0;

            List<String> endpoints = new ArrayList<>();

            String mgmtHostname = response.getDefaultManagementHostname();
            String mgmtUsername = response.getBroker().getMsgVpns().get(0).getManagementAdminLoginCredential().getUsername();
            String mgmtPassword = response.getBroker().getMsgVpns().get(0).getManagementAdminLoginCredential().getPassword();

            for (ConnectionEndpoint endpoint : response.getServiceConnectionEndpoints()) {
                for (String hostname : endpoint.getHostNames()) {
                    for (ServiceConnectionEndpointPort port : endpoint.getPorts()) {
                        if (port.getProtocol() == ServiceConnectionEndpointPort.ProtocolEnum.SERVICESMFTLSLISTENPORT) {
                            endpoints.add(hostname + ":" + port.getPort());
                        } else if (port.getProtocol() == ServiceConnectionEndpointPort.ProtocolEnum.SERVICEMANAGEMENTTLSLISTENPORT) {
                            mgmtPort = port.getPort();
                        }
                    }
                }
            }

            infoTab.init(vpnName, username, password, endpoints);
            queueTab.init(vpnName, mgmtHostname, mgmtPort, mgmtUsername, mgmtPassword);
            tryMePanel.init(endpoints, vpnName, username, password);
        }
    }

}
