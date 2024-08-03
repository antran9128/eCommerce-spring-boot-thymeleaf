package com.shopme.admin.brand;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;

import com.shopme.admin.user.export.AbstractExporter;
import com.shopme.common.entity.Brand;

public class BrandCsvExporter extends AbstractExporter{
	public void export(List<Brand> listBrands, HttpServletResponse response) 
			throws IOException {
		
		super.setResponseHeader(response, "text/csv", ".csv", "categories_");

		Writer writer = new OutputStreamWriter(response.getOutputStream(), "utf-8");
		writer.write('\uFEFF');
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(writer, 
				CsvPreference.STANDARD_PREFERENCE);

		String[] csvHeader = {"Brand ID", "Brand Name", "Categories of Brand"};
		String[] fieldMapping = {"id", "name", "categories"};

		csvWriter.writeHeader(csvHeader);

		for (Brand brand : listBrands) {
			brand.setName(brand.getName());
			brand.setCategories(brand.getCategories());
			csvWriter.write(brand, fieldMapping);
		}

		csvWriter.close();
		
		
	}
}
