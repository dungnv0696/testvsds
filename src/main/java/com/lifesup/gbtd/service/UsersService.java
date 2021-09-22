package com.lifesup.gbtd.service;

import com.lifesup.gbtd.dto.object.UsersDto;
import com.lifesup.gbtd.repository.UsersRepository;
import com.lifesup.gbtd.service.inteface.IUsersService;
import com.lifesup.gbtd.util.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService extends BaseService implements IUsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public List<UsersDto> getAllUserNameAndName() {
        return super.mapList(usersRepository.findByStatus(Const.STATUS.ACTIVE.intValue()), UsersDto.class);
    }
}
