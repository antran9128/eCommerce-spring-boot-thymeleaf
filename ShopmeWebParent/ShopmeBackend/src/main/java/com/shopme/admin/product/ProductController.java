package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;

@Controller
public class ProductController {
	@Autowired
	private ProductService service;
	@Autowired
	private BrandService brandService;
	
	
	@GetMapping("/products")
	public String listAll(Model model) {
		List<Product> listProducts = service.listAll();
		model.addAttribute("moduleUrl", "/products");
		model.addAttribute("listProducts", listProducts);
		return "products/products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAll();
		
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		
		return "products/product_form";
	}
	
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes re, @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		
			product.setMainImage(fileName);
		
			Product savedProduct = service.save(product);
			
			String uploadDir = "../product-images/" + savedProduct.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		} else {
			service.save(product);
		}
		
		re.addFlashAttribute("messageSuccess", "The product has been saved succesfully.");
		return "redirect:/products";
	}
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateCategoryEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes re) {
		service.updateProductEnabledStatus(id, enabled);
		
		String status = enabled ? "enabled" : "disabled";
		String message = "The Product ID " + id + " has been " + status;
		re.addFlashAttribute("messageSuccess", message);
		return "redirect:/products";
		
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes re, Model model) {
		try {
			service.delete(id);
			String message = "The product id " + id + " has been deleted successfully.";
			re.addFlashAttribute("messageSuccess", message);
		} catch (ProductNotFoundException e) {
			re.addFlashAttribute("messageSuccess", e.getMessage());
		}
		return "redirect:/products";
	}
	
	
}
