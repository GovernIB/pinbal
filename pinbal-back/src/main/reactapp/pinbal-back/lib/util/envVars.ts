export const envVar = (name: string, envVars: any) => {
    const runtimeConfig = (window as any).__RUNTIME_CONFIG__;
    if (runtimeConfig && runtimeConfig.hasOwnProperty(name)) {
        return runtimeConfig[name];
    } else {
        return envVars[name];
    }
};
