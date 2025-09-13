
package com.recicar.marketplace.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        if (isDatabaseEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Map<String, Object>>> typeReference = new TypeReference<List<Map<String, Object>>>() {};
            InputStream inputStream = TypeReference.class.getResourceAsStream("/all-vehicles-model.json");
            List<Map<String, Object>> vehicles = mapper.readValue(inputStream, typeReference);

            for (Map<String, Object> vehicle : vehicles) {
                String makeName = (String) vehicle.get("make");
                Long makeId = jdbcTemplate.queryForObject("INSERT INTO car_make (name) VALUES (?) RETURNING id", new Object[]{makeName}, Long.class);

                List<Map<String, Object>> models = (List<Map<String, Object>>) vehicle.get("models");
                if (models != null) {
                    for (Map<String, Object> model : models) {
                        String modelName = (String) model.get("model");
                        Long modelId = jdbcTemplate.queryForObject("INSERT INTO car_model (name, make_id) VALUES (?, ?) RETURNING id", new Object[]{modelName, makeId}, Long.class);

                        List<Map<String, Object>> trims = (List<Map<String, Object>>) model.get("trims");
                        if (trims != null) {
                            for (Map<String, Object> trim : trims) {
                                String trimName = (String) trim.get("trim");
                                jdbcTemplate.update("INSERT INTO car_trim (name, model_id) VALUES (?, ?)", trimName, modelId);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isDatabaseEmpty() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM car_make", Integer.class) == 0;
    }
}
