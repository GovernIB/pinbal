# ![Logo](https://github.com/GovernIB/pinbal/raw/master/assets/pinbal_logo.png) PINBAL
La Plataforma d'Interoperabilitat de les Illes Balears (PINBAL) és una solució tecnològica desenvolupada pel Govern de les Illes Balears que fa possible la interoperabilitat entre les administracions balears i la resta de l'Estat. PINBAL permet realitzar consultes (ja sigui a través d'una aplicació web o mitjançant serveis web) al serveis disponibles a través de la Plataforma d'Intermediació de l'Estat i als serveis propis definits en l'àmbit de la Comunitat Autònoma de les Illes Balears.
## <a name="docs"></a> Documentació
* [Manual d'instal·lació](https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/00_pinbal_instalar.pdf)
* [Manual de l'usuari administrador](https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/01_pinbal_usuari_admin.pdf)
* [Manual de l'usuari representant](https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/02_pinbal_usuari_representant.pdf)
* [Manual de l'usuari delegat](https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/03_pinbal_usuari_delegat.pdf)
* [Manual de l'usuari auditor](https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/04_pinbal_usuari_auditor.pdf)
* [Manual de l'usuari superauditor](https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/05_pinbal_usuari_superauditor.pdf)
* [Manual d'integració](https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/10_pinbal_integracio.pdf)
* [Manual d'integració amb API REST](https://github.com/GovernIB/pinbal/raw/pinbal-1.4/doc/pdf/11_pinbal_integracio_rest.pdf)
## <a name="versions"></a> Versions
- <a name="v_estable"></a> Versió Estable: __pinbal-1.4.40__ (tag [PINBAL 1.4.40](https://github.com/GovernIB/pinbal/releases/tag/v1.4.40))
- <a name="b_activa"></a> Versió Desenvolupament: __pinbal-1.4.41__ (branca [pinbal-dev](https://github.com/GovernIB/pinbal/tree/pinbal-dev))  
 
- Versió actual del client REST: __1.4.40__ ([pinbal-client-1.4.40.jar](https://github.com/GovernIB/maven/raw/gh-pages/maven/es/caib/pinbal/pinbal-client/1.4.40/pinbal-client-1.4.40.jar))  
    ```
    <dependency>  
        <groupId>es.caib.pinbal</groupId>  
        <artifactId>pinbal-client</artifactId>  
        <version>1.4.40</version>  
    </dependency>
    ```
- Configuració del client REST:
  - Adreça base del servei: https://SERVER/pinbalapi (substituïr SERVER per l'adreça que correspongui)
  - Tipus d'autenticació: BASIC
