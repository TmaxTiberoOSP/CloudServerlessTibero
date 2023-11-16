package com.tmax.serverless.manager.context;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DBContactInfo {
    String alias;
    String podName;
    String podIP;
    String hostIP;
}
