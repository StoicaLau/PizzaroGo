package com.pizzaro_go.menu_product.service;

import com.pizzaro_go.common.dtos.MessageResponse;
import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.menu_product.dtos.MenuProductRequest;
import com.pizzaro_go.menu_product.dtos.MenuProductResponse;
import com.pizzaro_go.menu_product.entity.MenuProduct;
import com.pizzaro_go.menu_product.mapper.IMenuProductMapper;
import com.pizzaro_go.menu_product.repository.IMenuProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for menu product operations.
 */
@Service
public class MenuProductService {
    @Autowired
    private IMenuProductRepository menuProductRepository;

    @Autowired
    private IMenuProductMapper menuProductMapper;

    private final Logger log = LoggerFactory.getLogger(MenuProductService.class);

    /**
     * Retrieves all menu products.
     *
     * @return a list of MenuProductResponse objects representing the menu products
     * @throws PGException if a repository error occurs during retrieval
     */
    public List<MenuProductResponse> getAll() throws PGException {
        this.log.info("Retrieve all menu products");
        try {
            return this.menuProductMapper.toResponseList(this.menuProductRepository.findAll());
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when retrieve all menu products ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

    /**
     * Updates an existing menu product.
     *
     * @param menuProductRequest the request containing updated menu product details
     * @return a MessageResponse with the updated menu product ID
     * @throws PGException if a repository error occurs during update
     */
    public MessageResponse update(MenuProductRequest menuProductRequest) throws PGException {
        Long menuProductId = menuProductRequest.getId();
        this.log.info("Updating the menu_product with id: {}", menuProductId);
        try {
            MenuProduct menuProductToUpdate = this.menuProductMapper.toEntity(menuProductRequest);
            MenuProduct updatedMenuProduct = this.menuProductRepository.save(menuProductToUpdate);
            return new MessageResponse(updatedMenuProduct.getId().toString());
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when updating the menu product  with id: " + menuProductId + " ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

    /**
     * Creates a new menu product.
     *
     * @param menuProductRequest the request containing new menu product details
     * @return a MessageResponse with the new menu product ID
     * @throws PGException if a repository error occurs during save
     */
    public MessageResponse create(MenuProductRequest menuProductRequest) throws PGException {
        this.log.info("Creating a new menu product item: {}", menuProductRequest.getName());
        try {
            MenuProduct menuProduct = this.menuProductMapper.toEntity(menuProductRequest);
            MenuProduct savedMenuProduct = this.menuProductRepository.save(menuProduct);
            return new MessageResponse(savedMenuProduct.getId().toString());
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when creating new menu product -> " + e.getMessage();
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Deletes a menu product by its ID.
     *
     * @param id the ID of the menu product
     * @return a MessageResponse confirming deletion
     * @throws PGException if the menu product is not found or a repository error
     *                     occurs
     */
    public MessageResponse deleteById(Long id) throws PGException {
        this.log.info("Delete the menu product with id: {}", id);
        try {
            if (!this.menuProductRepository.existsById(id)) {
                throw new PGException("MenuProduct with id " + id + " not found");
            }
            this.menuProductRepository.deleteById(id);
            return new MessageResponse("Menu product successfully deleted!");

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when deleting the menu product with id: " + id + " ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();
            throw new PGException(errorMsg);
        }
    }

    /**
     * Deletes all menu products.
     *
     * @return a MessageResponse confirming all menu products were deleted
     * @throws PGException if a repository error occurs
     */
    public MessageResponse deleteAll() throws PGException {
        this.log.info("Truncating entire menu product table to reset IDs.");
        try {
            this.menuProductRepository.truncateTable();
            return new MessageResponse("Entire menu successfully deleted and IDs reset!");

        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when deleting entire menu ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

}
