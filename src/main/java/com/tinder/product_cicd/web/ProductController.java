package com.tinder.product_cicd.web;

import com.tinder.product_cicd.product.Product;
import com.tinder.product_cicd.service.ProductService;
import com.tinder.product_cicd.web.dto.ProductDto;
import com.tinder.product_cicd.web.mapper.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public Page<ProductDto> list(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> p = service.listAll(pageable);
        return p.map(ProductMapper::toDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> get(@PathVariable Long id) {
        return service.getById(id)
                .map(ProductMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto dto) {
        Product created = service.create(ProductMapper.toEntity(dto));
        ProductDto out = ProductMapper.toDto(created);
        return ResponseEntity.created(URI.create("/api/products/" + out.getId())).body(out);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        try {
            Product updated = service.update(id, ProductMapper.toEntity(dto));
            return ResponseEntity.ok(ProductMapper.toDto(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<ProductDto> search(@RequestParam String q) {
        return service.searchByName(q).stream().map(ProductMapper::toDto).collect(Collectors.toList());
    }
}
