package com.pizzaro_go.stock.service;

import com.pizzaro_go.common.exceptions.PGException;
import com.pizzaro_go.common.exceptions.RepositoryException;
import com.pizzaro_go.stock.dtos.StockResponse;
import com.pizzaro_go.stock.entity.Stock;
import com.pizzaro_go.stock.mapper.IStockMapper;
import com.pizzaro_go.stock.repository.IStockRepository;
import com.pizzaro_go.fileimport.excel.entities.StockFileData;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Imports stocks from an Excel file.
     *
     * @param file the Excel file containing stock data
     * @throws PGException if an error occurs during processing
     */
    public void importStocks(MultipartFile file) throws PGException {
        this.log.info("Importing stocks from Excel file.");
        try (InputStream stream = file.getInputStream()) {
            List<StockFileData> stockFileDataList = Poiji.fromExcel(stream, PoijiExcelType.XLSX, StockFileData.class);

            List<Stock> stocks = stockFileDataList.stream()
                    .map(this.stockMapper::toEntity)
                    .toList();

            this.stockRepository.saveAll(stocks);
        } catch (IOException e) {
            String errorMsg = "Error processing Excel file -> " + e.getMessage();
            this.log.error(errorMsg, e);
            throw new PGException(errorMsg);
        } catch (RepositoryException e) {
            String errorMsg = "Error saving imported stocks -> " + e.getMessage();
            this.log.error(errorMsg, e);
            throw new PGException(errorMsg);
        }
    }

}
