package es.ual.dra.autodiagnostico.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.ual.dra.autodiagnostico.model.entitity.Product;
import es.ual.dra.autodiagnostico.model.entitity.Vehicle;
import es.ual.dra.autodiagnostico.repository.VehicleRepository;

import java.io.IOException;

/**
 * Servicio encargado de realizar el scraping de datos de vehículos y sus
 * productos asociados.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleScraperService {

        private final VehicleRepository vehicleRepository;

        /**
         * Realiza el scraping de una URL dada y guarda el vehículo y sus productos en
         * la base de datos.
         * 
         * @param url La URL objetivo para el scraping.
         * @return El vehículo persistido con sus productos.
         * @throws IOException Si ocurre un error de conexión.
         */
        @Transactional
        public void scrapeAndSave() throws IOException {
                final String url = "https://www.ultimatespecs.com/es";
                log.info("Iniciando scraping de la URL: {}", url);

                // 1. Configuración de Jsoup: Conexión y parseo
                // Se utiliza un User-Agent para evitar bloqueos por parte del servidor.
                Document doc = Jsoup.connect(url)
                                .userAgent(
                                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                                .timeout(10000)
                                .get();

                // 2. Lógica de Extracción: Captura de datos generales del vehículo
                // NOTA: Los selectores CSS son representativos y deben ajustarse al DOM real de
                // la web objetivo.
                Elements brands = doc.select(".home_brands");

                // Iterate through bands represented as a matrix with divs
                for (Element brand : brands) {
                        String brandName = brand.select(".home_brand").text();
                        String brandImage = brand.select(".home_brand_image").attr("src");
                        String brandUrl = brand.select(".home_brand_link").attr("href");

                }
        }
}
