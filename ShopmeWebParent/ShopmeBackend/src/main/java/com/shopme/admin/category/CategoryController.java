package com.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import com.shopme.admin.category.export.*;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.util.CategoryPageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class CategoryController {
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/categories")
	public String getAllCategories(@Param("sortDir") String sortDir, Model model) {
		return listByPage(1, sortDir, null, model);
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		model.addAttribute("category", new Category());
		model.addAttribute("pageTitle", "Create New Category");
		model.addAttribute("listCategories", listCategories);
		return "categories/category_form";
	}
	
	
	@GetMapping("/categories/page/{pageNum}") 
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, 
			@Param("sortDir") String sortDir, 
			@Param("keyword") String keyword,
			Model model) {
		
		if (sortDir ==  null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		
		List<Category> listCategories = categoryService.listByPage(pageInfo, pageNum, sortDir, keyword);
		long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
		if (endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("reverseSortDir", reverseSortDir);
		
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("moduleURL", "/categories");

		return "categories/categories";
	}
	
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, 
			@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes ra) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());			
			category.setImage(fileName);
			Category savedCategory = categoryService.save(category);			
			String uploadDir = "../category-images/" + savedCategory.getId();			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);				
		} else {
			categoryService.save(category);
		}
				
		ra.addFlashAttribute("message", "The category has been saved successfully.");
		return "redirect:/categories";
	}
	
	@GetMapping("categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
			Category cat = categoryService.getById(id);
			

			model.addAttribute("category", cat);
			model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");
			
			return "categories/category_form";
		} catch (CategoryNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/categories";
		}
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryStatus(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") boolean status,
			Model model, RedirectAttributes redirectAttributes) {
		categoryService.updateUserStatus(id, status);

		String enable = status ? "enabled" : "disabled";

		redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been " + enable + ".");

		return "redirect:/categories";
	}
	
	@GetMapping("/categories/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		CategoryCsvExporter exporter = new CategoryCsvExporter();
		exporter.export(listCategories, response);
		
		
	}
	
	@GetMapping("/categories/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		
		List<Category> listCategories = categoryService.listAll("asc");
	
		CategoryExcelExporter exporter = new CategoryExcelExporter();
		
		
		exporter.export(listCategories, response);
	}
	
	@GetMapping("/categories/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		
		List<Category> listCategories = categoryService.listAll("asc");
		
		CategoryPdfExporter exporter = new CategoryPdfExporter();
				
		exporter.export(listCategories, response);
		
	}
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name = "id") Integer id, 
			Model model,
			RedirectAttributes redirectAttributes) {	
		
		try {
			categoryService.delete(id);
									
			String categoryDir = "../category-images/" + id;
					
			FileUploadUtil.removeDir(categoryDir);
					
			redirectAttributes.addFlashAttribute("messageSuccess", 
					"The category ID " + id + " has been deleted successfully");
			
			
		} catch (CategoryNotFoundException ex) {
			redirectAttributes.addFlashAttribute("messageError", ex.getMessage());
		}

		return "redirect:/categories";
	}

	
}
