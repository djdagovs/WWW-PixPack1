--Image@ExDaCe
--TSearch2 has been build

--login

\c exdace

DROP SCHEMA image CASCADE;
REVOKE ALL ON pg_ts_cfg FROM image;
REVOKE ALL ON pg_ts_cfgmap FROM image;
REVOKE ALL ON pg_ts_dict FROM image;
REVOKE ALL ON pg_ts_parser FROM image;
DROP USER image;

CREATE USER image PASSWORD 'image';
CREATE SCHEMA image;
ALTER SCHEMA image OWNER TO image;
