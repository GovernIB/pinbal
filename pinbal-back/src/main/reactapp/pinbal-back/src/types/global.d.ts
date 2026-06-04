declare global {
  interface Window {
    __MANIFEST__?: {
      "Build-Timestamp": string;
      "Implementation-Vendor": string;
      "Implementation-SCM-Branch": string;
      "Implementation-SCM-Revision": string;
      "Implementation-Version": string;
      "Manifest-Version": string;
      "Created-By": string;
      "Build-Jdk-Spec": string;
    };
    __RUNTIME_CONFIG__?: string;
  }
}

export {};
