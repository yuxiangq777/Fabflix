DELIMITER $$  
CREATE FUNCTION parse_id (id char(100)) 
RETURNS char(100)
DETERMINISTIC
BEGIN 
	DECLARE temp char(100) DEFAULT NULL; 
	DECLARE temp2 char(100) DEFAULT NULL; 
	set temp= substring(id,1,3); 
	set temp2= substring(id,4); 
	return concat(temp,cast((cast(temp2 as unsigned)+1) as char));
END 
$$ 
CREATE PROCEDURE add_movie (IN t char(100), IN  y INT, IN d char(100), IN star_name char(100), IN genre_name char(100)) 
BEGIN 
	DECLARE message char(100) DEFAULT NULL;
	DECLARE ftitle char(100) DEFAULT NULL; 
	DECLARE max_id char(10) DEFAULT NULL;
	DECLARE new_id char(10) DEFAULT NULL;
	DECLARE message1 char(100) DEFAULT NULL;
	DECLARE sid char(10) DEFAULT NULL; 
	DECLARE message2 char(100) DEFAULT NULL;
	DECLARE max_ida char(10) DEFAULT NULL;
	DECLARE gid int(11) DEFAULT NULL; 
	DECLARE message3 char(100) DEFAULT NULL;
	DECLARE max_id_ int(11) DEFAULT NULL;
	select title into ftitle from movies where title=t and year=y and director=d; 
	IF ftitle is NOT NULL THEN SET message= 'Failed! Duplicate movie.'; 
	ELSE 
		select max(id) into max_id from movies;
		select parse_id(max_id) into new_id;
		insert into movies values(new_id,t,y,d);
		set message1=concat('Successful! New movie ID= ', new_id, ',  ');
		select id into sid from stars where name=star_name; 
		IF sid is NULL THEN 
			select max(id) into max_ida from stars;
			select parse_id(max_ida) into sid;
			insert into stars(id,name) values(sid,star_name);
			set message2=concat('new star ID= ', sid, ',  ');
		ELSE 
			set message2=concat('existing star ID= ', sid, ',  ');
		END IF;
		insert into stars_in_movies values(sid,new_id);
		select id into gid from genres where name=genre_name; 
		IF gid is NULL THEN 
			insert into genres(name) values(genre_name);
			select id into gid from genres where name=genre_name; 
			set message3=concat('new genre ID= ', convert(gid, char));
		ELSE 
			set message3=concat('existing genre ID= ', convert(gid, char));
		END IF;
		insert into stars_in_movies values(sid,new_id);
		insert into genres_in_movies values(gid,new_id);
		insert into ratings values(new_id,1.0,1);
		set message= concat(message1,message2,message3);
	END IF; 
	select message as answer;
END 
$$ 
DELIMITER ; 