package com.meetball.demo.persistence;

import com.meetball.demo.domain.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    public void insertOrder(Order order);

    public List<String> getOrderList(String userName);

    public Order getOrderInfo(String orderId);

    //public List<Match> getAllMatch(@Param("method")int method);

    //public void updateMatch(Match match);

    //public void deleteById(Match match);

    //public void insertMatch(Match match);

    //public void updateUser(User user);

    //public Match getMatchInfo(@Param("matchID") int matchID);

}
