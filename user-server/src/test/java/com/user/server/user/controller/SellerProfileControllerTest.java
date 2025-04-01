package com.user.server.user.controller;

import com.common.config.api.controller.APIExceptionHandler;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(SellerProfileController.class)
@Import(APIExceptionHandler.class)
class SellerProfileControllerTest {

}