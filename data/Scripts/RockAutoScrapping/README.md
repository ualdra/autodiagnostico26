# RockAuto Scraper Scripts

This folder contains the necessary scripts for scraping vehicle and parts data from RockAuto.

## Scripts

### 1. `index.js`
- **Purpose**: Main script for scraping RockAuto data, including vehicle details, parts, prices, and descriptions.
- **Usage**:
  ```bash
  node . <url>
  ```
  Replace `<url>` with the RockAuto page URL you want to scrape.
- **Example**:
  ```bash
  node . "https://www.rockauto.com/es/catalog/acura,2026,adx,1.5l+l4+turbocharged,3459253,heat+&+air+conditioning,cabin+air+filter,6832"
  ```
- **Output**: Generates a JSON file (`vehicles_rockauto.json`) in the `frontend/src/assets/mocks/` directory.

## Notes
- Ensure that Node.js is installed on your system.
- The `index.js` script is the only required script for production use. Debug scripts have been removed as they were used for troubleshooting purposes.
- The output JSON file contains structured data about the vehicle and its parts, including prices and descriptions.