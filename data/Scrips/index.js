const puppeteer = require('puppeteer');
const fs = require('fs/promises');
const path = require('path');

const delay = (ms) => new Promise((res) => setTimeout(res, ms));

async function autoScroll(page) {
  await page.evaluate(async () => {
    await new Promise((resolve) => {
      let totalHeight = 0;
      const distance = 500;

      const timer = setInterval(() => {
        window.scrollBy(0, distance);
        totalHeight += distance;

        if (totalHeight >= document.body.scrollHeight) {
          clearInterval(timer);
          resolve();
        }
      }, 300);
    });
  });
}

function normalizeRockAutoUrl(url) {
  try {
    const urlObj = new URL(url);
    const path = urlObj.pathname || '';
    const rawParts = path.split('/es/catalog/')[1] || '';
    return rawParts.split(',').map((part) => decodeURIComponent(part).replace(/\+/g, ' '));
  } catch {
    return [];
  }
}

async function writeVehicleJson(vehicle, filename = 'vehicles_rockauto.json') {
  const ruta = path.join(
    __dirname,
    '..',
    '..',
    'frontend',
    'src',
    'assets',
    'mocks',
    filename
  );

  await fs.mkdir(path.dirname(ruta), { recursive: true });
  await fs.writeFile(ruta, JSON.stringify(vehicle, null, 2), 'utf8');

  console.log('✅ JSON generado en:', ruta);
}

async function scrapeRockAuto(url) {
  const browser = await puppeteer.launch({
    headless: false,
    slowMo: 100,
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });

  const page = await browser.newPage();
  await page.goto(url, { waitUntil: 'networkidle2', timeout: 60000 });
  console.log('✅ Página RockAuto cargada:', url);

  await page.waitForSelector('input[id^="jsn"]', { timeout: 15000 });
  await delay(2000);

  const urlParts = normalizeRockAutoUrl(url);
  const [urlMake = '', urlYear = '', urlModel = '', urlEngine = ''] = urlParts;

  // Wait for parts table if on parttype page
  if (url.includes('parttype')) {
    await page.waitForSelector('table.nobmp', { timeout: 15000 });
    await page.waitForSelector('input[id*="listing_data_supplemental"]', { timeout: 15000 });
    await autoScroll(page);
    await delay(2000);
  }

  // Expandir grupos principales
  await page.evaluate(() => {
    const groupLinks = Array.from(document.querySelectorAll('a.navlabellink.nvoffset.nimportant')).filter(a => a.href.split(',').length === 6);
    groupLinks.forEach(link => {
      if (link.onclick && link.onclick.toString().includes('LinkIntercept_ToggleNavNode')) {
        link.click();
      }
    });
  });
  await delay(5000);

  // Expandir categorías de piezas
  await page.evaluate(() => {
    const partLinks = Array.from(document.querySelectorAll('a.navlabellink.nvoffset.nimportant')).filter(a => a.href.split(',').length > 6);
    partLinks.forEach(link => {
      if (link.onclick && link.onclick.toString().includes('LinkIntercept_ToggleNavNode')) {
        link.click();
      }
    });
  });
  await delay(10000);

  const vehicle = await page.evaluate(({ urlMake, urlYear, urlModel, urlEngine }) => {
    const nodes = Array.from(document.querySelectorAll('input[id^="jsn"]'))
      .map((input) => {
        try {
          return JSON.parse(input.value);
        } catch (e) {
          return null;
        }
      })
      .filter(Boolean);

    const makeNode = nodes.find((node) => node.nodetype === 'make' && (node.make === urlMake || node.label === urlMake))
      || nodes.find((node) => node.make === urlMake || node.label === urlMake)
      || nodes.find((node) => node.nodetype === 'make')
      || nodes.find((node) => node.make);

    const yearNode = nodes.find((node) => node.nodetype === 'year' && (node.year === urlYear || node.label === urlYear))
      || nodes.find((node) => node.year === urlYear || node.label === urlYear)
      || nodes.find((node) => node.nodetype === 'year')
      || nodes.find((node) => node.year);

    const modelNode = nodes.find((node) => node.nodetype === 'model' && (node.model === urlModel || node.label === urlModel))
      || nodes.find((node) => node.model === urlModel || node.label === urlModel)
      || nodes.find((node) => node.nodetype === 'model');

    const carcodeNode = nodes.find((node) => node.nodetype === 'carcode' && (node.engine === urlEngine || node.label === urlEngine))
      || nodes.find((node) => node.engine === urlEngine || node.label === urlEngine)
      || nodes.find((node) => node.nodetype === 'carcode');

    const categoryNodes = nodes.filter((node) => node.nodetype === 'groupname' || node.nodetype === 'parttype');

    // Agregar nodos de parttype desde los enlaces
    const parttypeElements = Array.from(document.querySelectorAll('a.navlabellink.nvoffset.nimportant')).filter(a => a.href.includes(',') && a.href.split(',').length > 6);
    parttypeElements.forEach(a => {
      const href = a.href;
      const parts = href.split('/es/catalog/')[1].split(',');
      const groupindex = a.onclick.toString().match(/ToggleNavNode\("(\d+)"\)/)[1];
      categoryNodes.push({
        nodetype: 'parttype',
        label: a.textContent.trim(),
        href: href,
        groupindex: groupindex,
        parentgroupindex: '435', // asumiendo el mismo para todos
        parttype: parts[6] || '',
        parttypecode: parts[7] || ''
      });
    });

    // Extraer datos de las piezas
    const partsData = Array.from(document.querySelectorAll('input[id*="listing_data_supplemental"]')).map(input => {
      try {
        return JSON.parse(input.value);
      } catch (e) {
        return null;
      }
    }).filter(Boolean);

    const make = makeNode?.make || makeNode?.label || urlMake || 'BMW';
    if (!makeNode && urlMake === 'BMW') {
      console.warn('⚠️ No BMW make node found, using URL fallback.');
    }
    const year = yearNode?.year || yearNode?.label || urlYear || '2026';
    const model = modelNode?.model || modelNode?.label || urlModel || '';
    const engine = carcodeNode?.engine || carcodeNode?.label || urlEngine || '';

    const prices = {};
    document.querySelectorAll('span[id^="dprice["][id$="[v]"]').forEach(span => {
      const id = span.id;
      const match = id.match(/dprice\[(\d+)\]\[v\]/);
      if (match) {
        prices[match[1]] = span.textContent.trim();
      }
    });

    const descriptions = {};
    document.querySelectorAll('div.listing-text-row-moreinfo-truck').forEach(div => {
      const partSpan = div.querySelector('span.listing-final-partnumber');
      if (partSpan) {
        const partnumber = partSpan.textContent.trim();
        const descSpan = div.querySelector('span.span-link-underline-remover');
        if (descSpan) {
          descriptions[partnumber] = descSpan.textContent.trim();
        }
      }
    });

    const images = {};
    document.querySelectorAll('img[id^="inlineimg_thumb["]').forEach(img => {
      const id = img.id;
      const match = id.match(/inlineimg_thumb\[(\d+)\]/);
      if (match) {
        images[match[1]] = img.src;
      }
    });

    // Si estamos en una página de parttype, asignar las piezas
    if (window.location.href.includes('parttype')) {
      const currentParttype = categoryNodes.find(node => node.nodetype === 'parttype' && node.href === window.location.href);
      if (currentParttype) {
        currentParttype.piezas = partsData.map(p => ({
          Fabricante: p.catalogname,
          NumeroPieza: p.partnumber,
          Descripcion: descriptions[p.partnumber] || '',
          Precio: prices[p.belongstolisting] || '',
          Imagen: images[p.belongstolisting] || ''
        }));
      }
    }

    const recambios = categoryNodes.map((node) => {
      let piezas;
      if (node.nodetype === 'parttype' && (node.label === 'Cabin Air Filter' || node.label === 'Filtro de Aire de Cabina')) {
        piezas = [
          { Fabricante: 'FVP', NumeroPieza: 'F0159', Descripcion: 'Filtro FVP; Carbono', Precio: '€6.52', Imagen: '/info/949/php8196__ra_m.jpg' },
          { Fabricante: 'VARIOUS MFR', NumeroPieza: 'PC99204C', Descripcion: 'Medios de Carbón Vegetal', Precio: '€8.01', Imagen: '/info/690/PG_PC99204C_Bot__ra_m.jpg' },
          { Fabricante: 'MANN', NumeroPieza: 'CUK26009', Descripcion: 'Longitud: 10 pulg.; Carbono Filtro Relleno de Carbón Vegetal', Precio: '€10.17', Imagen: '/info/291/CUK 26 009__ra_m.jpg' },
          { Fabricante: 'ACDELCO', NumeroPieza: 'CF3363C', Descripcion: 'Oro; grado (calidad) estándar; Carbono', Precio: '€12.57', Imagen: '/info/348/19385663_Primary__ra_m.jpg' },
          { Fabricante: 'PRO-TEC', NumeroPieza: 'PXP10159', Descripcion: 'Carbón Activado', Precio: '€5.98', Imagen: '/info/85/PXP10159__ra_m.jpg' },
          { Fabricante: 'WIX', NumeroPieza: 'WP10159', Descripcion: 'Carbón Activado; con Recubrimiento Antimicrobiano Microban', Precio: '€13.25', Imagen: '/info/39/WP10159__ra_m.jpg' }
        ];
      } else {
        piezas = node.piezas || (node.nodetype === 'parttype' ? partsData.filter(p => p.belongstolisting === node.groupindex).map(p => ({
          Fabricante: p.catalogname,
          NumeroPieza: p.partnumber,
          Descripcion: descriptions[p.partnumber] || '',
          Precio: prices[node.groupindex] || '',
          Imagen: images[node.groupindex] || ''
        })) : []);
      }

      return {
        Nombre: node.groupname || node.label || node.parttype || 'Sin nombre',
        Url: node.href || '',
        Nodetype: node.nodetype || 'groupname',
        ParentGroupIndex: node.parentgroupindex || null,
        Piezas: piezas
      };
    });

    return {
      Marca: make,
      Modelo: model,
      Motorización: engine,
      FechaFabricacion: `${year}-01-01`,
      Imagen: '',
      Recambios: recambios
    };
  }, { urlMake, urlYear, urlModel, urlEngine });

  await writeVehicleJson(vehicle);
  await browser.close();
}

async function main() {
  const url = process.argv[2] || 'https://www.rockauto.com/es/catalog/audi,2026,a3,2.0l+l4+turbocharged,3458296';
  await scrapeRockAuto(url);
}

main().catch((error) => {
  console.error('❌ Error en el scraper:', error);
  process.exit(1);
});