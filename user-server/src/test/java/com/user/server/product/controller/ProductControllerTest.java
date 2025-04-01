package com.user.server.product.controller;

import com.common.config.api.constant.ErrorCode;
import com.common.config.api.controller.APIExceptionHandler;
import com.common.config.api.exception.GeneralException;
import com.common.config.api.exception.ProductNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.server.brand.entity.ProductBrand;
import com.user.server.file.dto.FileDto;
import com.user.server.product.dto.ResponseProductDto;
import com.user.server.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(ProductController.class)
@Import(APIExceptionHandler.class)
class ProductControllerTest {




}