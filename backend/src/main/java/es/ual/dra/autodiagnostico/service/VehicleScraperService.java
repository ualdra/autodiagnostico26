package es.ual.dra.optcg.service;

import es.ual.dra.optcg.model.entitity.Product;
import es.ual.dra.optcg.model.entitity.Vehicle;
import es.ual.dra.optcg.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        public Vehicle scrapeAndSave(String url) throws IOException {
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
                String brand = doc.select(".vehicle-brand").text();
                String model = doc.select(".vehicle-model").text();
                String image = doc.select(".vehicle-main-image").attr("src");

                // Datos adicionales (si están disponibles en el DOM)
                String yearStr = doc.select(".vehicle-year").text().replaceAll("[^0-9]", "");
                Integer year = yearStr.isEmpty() ? null : Integer.parseInt(yearStr);
                String engine = doc.select(".vehicle-engine").text();
                String fuel = doc.select(".vehicle-fuel").text();

                // 3. Mapeo a Entidad Vehicle
                Vehicle vehicle = Vehicle.builder()
                                .brand(brand)
                                .model(model)
                                .image(image)
                                .year(year)
                                .engine(engine)
                                .fuel(fuel)
                                .build();

                // 2. Lógica de Extracción: Captura de la lista de productos
                // Se buscan los elementos que contienen la información de los productos.
                Elements productElements = doc.select(".product-item");
                for (Element element : productElements) {
                        String productName = element.select(".product-name").text();
                        String productDesc = element.select(".product-description").text();
                        String priceStr = element.select(".product-price").text().replaceAll("[^0-9,.]", "")
                                        .replace(",", ".");
                        Double price = priceStr.isEmpty() ? 0.0 : Double.parseDouble(priceStr);

                        // 3. Mapeo a Entidad Product y asociación bidireccional
                        Product product = Product.builder()
                                        .name(productName)
                                        .description(productDesc)
                                        .price(price)
                                        .build();

                        vehicle.addProduct(product);
                }

                // 4. Persistencia de Datos: Guardado en cascada debido a CascadeType.ALL
                log.info("Guardando vehículo extraído: {} {}", brand, model);
                return vehicleRepository.save(vehicle);
        }
}
