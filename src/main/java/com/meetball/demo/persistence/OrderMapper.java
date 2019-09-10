package com.meetball.demo.persistence;

import com.meetball.demo.domain.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    public void insertOrder(Order order);

    public List<String> getUserOrderList(String userName);

    public List<String> getDriverOrderList(String userName);

    public Order getOrderInfo(String orderId);

    public boolean updateComment(String orderId, int star, String comment);

    public boolean addFavorite(String user, String driverName);

    //public List<Match> getAllMatch(@Param("method")int method);

    //public void updateMatch(Match match);

    //public void deleteById(Match match);

    //public void insertMatch(Match match);

    //public void updateUser(User user);

    //public Match getMatchInfo(@Param("matchID") int matchID);

}
