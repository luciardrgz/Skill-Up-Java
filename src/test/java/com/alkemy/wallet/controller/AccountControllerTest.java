package com.alkemy.wallet.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alkemy.wallet.dto.ResponseAccountDto;
import com.alkemy.wallet.dto.ResponseUserDto;
import com.alkemy.wallet.dto.UpdateAccountDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.alkemy.wallet.dto.ResponseUserBalanceDto;
import com.alkemy.wallet.mapper.IAccountMapper;
import com.alkemy.wallet.security.service.JwtUtils;
import com.alkemy.wallet.service.IAccountService;
import com.alkemy.wallet.service.IUserService;

import java.util.ArrayList;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = { AccountController.class })
@ContextConfiguration(classes = { AccountController.class })
class AccountControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	private IUserService userService;

	@MockBean
	private IAccountService accountService;

	@MockBean
	private IAccountMapper iAccountMapper;
	
	@MockBean
	private JwtUtils jwtUtils;

	@MockBean
	private AccountController accountController;

	private String uri = "/accounts";


	@Test
	void getAccountsBalance_GetRequestWithSpecificName_ResponseUserBalanceDto()
		throws Exception {
		String token = "token";

		when(accountService.getBalance(token))
			.thenReturn(new ResponseUserBalanceDto());

		mockMvc
			.perform(
				get(uri + "/balance")
					.header("authorization", "Bearer " + token))
			.andExpect(status().isOk());
	}

	@Test
	void updateAccount_PatchRequest_ResponseOk() throws Exception {
		String token = "token";
		long accountId = 60;
		UpdateAccountDto requestAccountDto = new UpdateAccountDto();
		requestAccountDto.setTransactionLimit(9999.0);

		when(accountService.updateAccount(accountService.findById(accountId), requestAccountDto, token))
				.thenReturn(new ResponseAccountDto());

		mockMvc
				.perform(
						patch(uri + "/" + accountId)
								.header("authorization", "Bearer " + token))
				.andExpect(status().isOk());
		/*
	  long id = 1;
      String content = "new updated content";
      MockHttpServletRequestBuilder builder =
              MockMvcRequestBuilders.patch("/articles/" + id)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .characterEncoding("UTF-8")
                                    .content(getArticleInJson(1, content));
      this.mockMvc.perform(builder)
                  .andExpect(MockMvcResultMatchers.status()
                                                  .isOk())
                  .andExpect(MockMvcResultMatchers.content()
                                                  .string("Article updated with content: " + content))
                  .andDo(MockMvcResultHandlers.print());
	}


	 */
	}
}
