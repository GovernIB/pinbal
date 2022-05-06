-- 150
CREATE TABLE pbl_config_group (
                                  code VARCHAR2(128 CHAR) NOT NULL,
                                  description_key VARCHAR2(512 CHAR),
                                  position NUMBER(3) NOT NULL,
                                  parent_code VARCHAR2(128 CHAR));
ALTER TABLE pbl_config_group ADD PRIMARY KEY (code);

CREATE TABLE pbl_config_type (
                                 code VARCHAR2(128 CHAR) NOT NULL,
                                 value VARCHAR2(2048 CHAR));
ALTER TABLE pbl_config_type ADD PRIMARY KEY (code);

CREATE TABLE pbl_config (
                            key VARCHAR2(256 CHAR) NOT NULL,
                            value VARCHAR2(2048 CHAR),
                            description_key VARCHAR2(2048 CHAR),
                            group_code VARCHAR2(128 CHAR) NOT NULL,
                            position NUMBER(3) NOT NULL,
                            source_property VARCHAR2(16 CHAR) NOT NULL,
                            type_code VARCHAR2(128 CHAR),
                            lastmodifiedby_codi VARCHAR2(64 CHAR),
                            lastmodifieddate TIMESTAMP);
ALTER TABLE pbl_config ADD PRIMARY KEY (key);
ALTER TABLE pbl_config ADD CONSTRAINT pbl_config_group_fk FOREIGN KEY (group_code) REFERENCES pbl_config_group (code);


grant select, update, insert, delete on pbl_config_group to www_pinbal;
grant select, update, insert, delete on pbl_config_type to www_pinbal;
grant select, update, insert, delete on pbl_config to www_pinbal;

-- 205
ALTER TABLE pbl_entitat_usuari ADD actiu NUMBER(1);