ALTER TABLE `phpbb_users` CHANGE `user_id` `user_id` INT NOT NULL AUTO_INCREMENT;

ALTER TABLE `phpbb_users` CHANGE `username` `username` VARCHAR( 128 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL;
ALTER TABLE `phpbb_users` CHANGE `user_password` `user_password` VARCHAR( 64 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL;