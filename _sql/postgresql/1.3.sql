--Image@ExDaCe
--TSearch2 has been build

--login

\c exdace

--Grant Database
GRANT ALL ON SCHEMA public TO image;

GRANT ALL ON image.account TO image;
GRANT ALL ON image.account_id_seq TO image;
GRANT ALL ON image.folder TO image;
GRANT ALL ON image.folder_id_seq TO image;
GRANT ALL ON image.extension TO image;
GRANT ALL ON image.extension_id_seq TO image;
GRANT ALL ON image.file TO image;
GRANT ALL ON image.file_id_seq TO image;


--TSearch 2 install (tsearch2 have to be installed at public)
--based on: http://www.sai.msu.su/~megera/postgres/gist/tsearch/V2/docs/tsearch-V2-intro.html

--GRANT VECTOR
GRANT SELECT ON pg_ts_cfg TO image;
GRANT SELECT ON pg_ts_cfgmap TO image;
GRANT SELECT ON pg_ts_dict TO image;
GRANT SELECT ON pg_ts_parser TO image;

