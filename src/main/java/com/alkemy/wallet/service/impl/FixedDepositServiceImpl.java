package com.alkemy.wallet.service.impl;

import com.alkemy.wallet.dto.FixedDepositDto;
import com.alkemy.wallet.dto.ResponseSimulateFixedDepositDto;
import com.alkemy.wallet.exceptions.BadRequestException;
import com.alkemy.wallet.mapper.FixedDepositMapper;
import com.alkemy.wallet.model.Account;
import com.alkemy.wallet.model.FixedTermDeposit;
import com.alkemy.wallet.repository.IAccountRepository;
import com.alkemy.wallet.repository.IFixedTermDepositRepository;
import com.alkemy.wallet.repository.IUserRepository;
import com.alkemy.wallet.security.service.JwtUtils;
import com.alkemy.wallet.service.IFixedDepositService;
import com.alkemy.wallet.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import static com.alkemy.wallet.model.ECurrency.ARS;
import static com.alkemy.wallet.model.ECurrency.USD;

@Service
public class FixedDepositServiceImpl implements IFixedDepositService {
    private static final int MIN_DAYS = 30;
    private IAccountRepository iAccountRepository;
    private IUserService iUserService;
    private IUserRepository userRepository;
    private IFixedTermDepositRepository iFixedTermDepositRepository;
    private FixedDepositMapper fixedDepositMapper;
    private JwtUtils jwtUtils;

    @Autowired
    public FixedDepositServiceImpl(
            IUserRepository userRepository,
            IFixedTermDepositRepository iFixedTermDepositRepository,
            FixedDepositMapper fixedDepositMapper,
            IAccountRepository iAccountRepository, IUserService iUserService, JwtUtils jwtUtils){
        this.userRepository = userRepository;
        this.iAccountRepository = iAccountRepository;
        this.iFixedTermDepositRepository = iFixedTermDepositRepository;
        this. iUserService = iUserService;
        this.jwtUtils = jwtUtils;
        this.fixedDepositMapper = fixedDepositMapper;
    }
    public String addFixedDeposit(String email, FixedDepositDto dto) throws Exception{

        Long userId = userRepository.findByEmail(email).getId();

        Account account = iAccountRepository.getReferenceByUserIdAndCurrency(userId,dto.getCurrency());

        if(account.getBalance() >= dto.getAmount()){

            FixedTermDeposit fixedTermDeposit = fixedDepositMapper.toEntity(dto);

            long compareDate = ChronoUnit.DAYS.between(Timestamp.from(fixedTermDeposit.getCreationDate().toInstant()).toInstant(),fixedTermDeposit.getClosingDate().toInstant());

            if(compareDate < MIN_DAYS )
                throw new BadRequestException("The minimum term is 30 days.");

            account.setBalance(account.getBalance() - dto.getAmount());
            Account accountSaved = iAccountRepository.save(account);
            fixedTermDeposit.setAccount(account);
            iFixedTermDepositRepository.save(fixedTermDeposit);

        }else{
            throw new BadRequestException("You don't create a fixed deposit with a lower balance.");
        }
        return HttpStatus.CREATED.getReasonPhrase();
    }

    @Override
    public ResponseSimulateFixedDepositDto simulateFixedDeposit(FixedDepositDto dto) {
        if (dto.getAmount() <= 0)
            throw new BadRequestException("Invalid amount.");
        if (dto.getCurrency() != ARS && dto.getCurrency() != USD )
            throw new BadRequestException("Invalid currency.");
        if (dto.getPeriod()<MIN_DAYS)
            throw new BadRequestException("The minimum term is 30 days.");
        return fixedDepositMapper.toSimulateFixedDeposit(dto);
    }
}
