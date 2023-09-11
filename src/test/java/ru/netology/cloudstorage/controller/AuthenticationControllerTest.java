package ru.netology.cloudstorage.controller;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.netology.cloudstorage.dto.request.AuthenticationRQ;
import ru.netology.cloudstorage.dto.response.AuthenticationRS;
import ru.netology.cloudstorage.service.AuthenticationService;

import static ru.netology.cloudstorage.TestData.AUTHENTICATION_RS;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;

    @Test
    public void authenticationControllerLoginTest() throws Exception {

        ResultActions resultActions = (ResultActions) Mockito.when(authenticationService.login(new AuthenticationRQ("11","1111"))).
                thenReturn(new ResponseEntity(AUTHENTICATION_RS, HttpStatus.OK));

//        String jsonText = "{\"login\":\"test@mail.ru\",\"password\":\"test\" }";
//        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(jsonText)).andExpect(MockMvcResultMatchers.status().isOk());
//       var result = resultActions.andReturn().getResponse();
//        Assertions.assertNotNull(result);
    }
}

