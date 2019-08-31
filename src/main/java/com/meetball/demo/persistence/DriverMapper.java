package com.meetball.demo.persistence;

import com.meetball.demo.domain.Driver;
import com.meetball.demo.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DriverMapper {
    public List<Driver> getDriverUnpassList();

    public boolean passDriver(String userName);

    public boolean deleteDriver(String userName);

    //public List<Match> getAllMatch(@Param("method")int method);

    //public void updateMatch(Match match);

    //public void deleteById(Match match);

    //public void insertMatch(Match match);

    //public void updateUser(User user);

    //public Match getMatchInfo(@Param("matchID") int matchID);

}
