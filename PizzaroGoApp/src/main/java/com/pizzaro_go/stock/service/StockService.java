package com.pizzaro_go.stock.service;

import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.stock.dtos.StockResponse;
import com.pizzaro_go.stock.mapper.IStockMapper;
import com.pizzaro_go.stock.repository.IStockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for stock operations.
 */
@Service
public class StockService {

    private final IStockRepository stockRepository;
    private final IStockMapper stockMapper;
    private final Logger log = LoggerFactory.getLogger(StockService.class);

    /**
     * Creates a new StockService with the given dependencies.
     *
     * @param stockRepository the repository used for stock persistence
     * @param stockMapper     the mapper used for converting between stock entities
     *                        and DTOs
     */
    public StockService(IStockRepository stockRepository, IStockMapper stockMapper) {
        this.stockRepository = stockRepository;
        this.stockMapper = stockMapper;
    }

    /**
     * Retrieves the entire stock.
     *
     * @return a list of StockResponse objects representing the stock items
     * @throws PGException if a repository error occurs during retrieval
     */
    public List<StockResponse> getAll() throws PGException {
        this.log.info("Retrieve  entire stock.");
        try {
            return this.stockMapper.toResponseList(this.stockRepository.findAll());
        } catch (RepositoryException e) {
            String errorMsg = "Error occurred when retrieve entire stock ->";
            this.log.error(errorMsg, e);

            errorMsg += e.getMessage();

            throw new PGException(errorMsg);
        }
    }

}
