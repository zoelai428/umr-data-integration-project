CREATE OR REPLACE FUNCTION calculate_distance(lat1 float, lon1 float, lat2 float, lon2 float, units varchar)
RETURNS float AS $dist$
    DECLARE
        dist float = 0;
        radlat1 float;
        radlat2 float;
        theta float;
        radtheta float;
    BEGIN
        IF lat1 = lat2 AND lon1 = lon2
            THEN RETURN dist;
        ELSE
            radlat1 = pi() * lat1 / 180;
            radlat2 = pi() * lat2 / 180;
            theta = lon1 - lon2;
            radtheta = pi() * theta / 180;
            dist = sin(radlat1) * sin(radlat2) + cos(radlat1) * cos(radlat2) * cos(radtheta);

            IF dist > 1 THEN dist = 1; END IF;

            dist = acos(dist);
            dist = dist * 180 / pi();
            dist = dist * 60 * 1.1515;

            IF units = 'K' THEN dist = dist * 1.609344; END IF;
            IF units = 'N' THEN dist = dist * 0.8684; END IF;

            RETURN dist;
        END IF;
    END;
$dist$ LANGUAGE plpgsql;


SELECT B.headline, B.description, B.date, U.headline, U.description, U.date
FROM
	(SELECT *
		FROM BIGFOOT_SIGHTING, REPORT, LOCATION
		WHERE BIGFOOT_SIGHTING.REPORT_ID = REPORT.ID and
        BIGFOOT_SIGHTING.location_id = location.id) AS B,

	(SELECT *
		FROM UFO_SIGHTING, REPORT, LOCATION
		WHERE UFO_SIGHTING.REPORT_ID = REPORT.ID and
        UFO_SIGHTING.location_id = location.id) AS U
		
WHERE EXTRACT(DAY FROM B.date) = EXTRACT(DAY FROM U.date) and
        EXTRACT(MONTH FROM B.date) = EXTRACT(MONTH FROM U.date) and
        EXTRACT(YEAR FROM B.date) = EXTRACT(YEAR FROM U.date) and 
        calculate_distance((b.latitude, b.longitude, u.latitude, u.longitude, 'M')) < 200