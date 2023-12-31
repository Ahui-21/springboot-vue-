package com.bjh.controller;

import com.bjh.entity.*;
import com.bjh.page.Page;
import com.bjh.service.InStoreService;
import com.bjh.service.PurchaseService;
import com.bjh.service.StoreService;
import com.bjh.utils.TokenUtils;
import com.bjh.utils.WarehouseConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/purchase")
@RestController
public class PurchaseController {


    @Autowired
    private StoreService storeService;

    @Autowired
    private PurchaseService purchaseService;
    //注入TokenUtils
    @Autowired
    private TokenUtils tokenUtils;

    //注入InStoreService
    @Autowired
    private InStoreService inStoreService;



    /*
    添加采购单
    */
    @RequestMapping("/purchase-add")
    public Result addPurchase(@RequestBody Purchase purchase) {
        //执行业务
        Result result = purchaseService.insertPurchase(purchase);
        //响应
        return result;
    }

    /*
     *
     * 查询所有仓库的接口
     * */

    @RequestMapping("/store-list")
    public Result storeList() {
        //执行业务
        List<Store> storeList = storeService.findAllStore();
        //响应
        return Result.ok(storeList);
    }

    /**
     * 分页查询采购单的url接口/purchase/purchase-page-list
     */
    @RequestMapping("/purchase-page-list")
    public Result purchasePageList(Page page, Purchase purchase) {
        //执行业务
        page = purchaseService.queryPurchasePage(page, purchase);
        //响应
        return Result.ok(page);
    }


    /**
     * 删除采购单的url接口/purchase/purchase-delete/{buyId}
     *
     * @PathVariable Integer buyId将路径占位符buyId的值赋值给参数变量buyId;
     */
    @RequestMapping("/purchase-delete/{buyId}")
    public Result deletePurchase(@PathVariable Integer buyId) {
        //执行业务
        Result result = purchaseService.deletePurchase(buyId);
        //响应
        return result;

    }

    /**
     * 修改采购单的url接口/purchase/purchase-update
     *
     * @RequestBody Purchase purchase将请求传递的json数据封装到参数Purchase对象;
     */
    @RequestMapping("/purchase-update")
    public Result updatePurchase(@RequestBody Purchase purchase){
        //执行业务
        Result result = purchaseService.updatePurchase(purchase);
        //响应
        return result;
    }

    /**
     * 添加入库单的url接口/purchase/in-warehouse-record-add
     */
    @RequestMapping("/in-warehouse-record-add")
    public Result addInStore(@RequestBody Purchase purchase,
                             @RequestHeader(WarehouseConstants.HEADER_TOKEN_NAME) String token){
        //获取当前登录的用户
        CurrentUser currentUser = tokenUtils.getCurrentUser(token);
        //获取当前登录的用户id -- 创建入库单的用户id
        int createBy = currentUser.getUserId();

        //创建InStore对象封装添加的入库单的信息
        InStore inStore = new InStore();
        inStore.setStoreId(purchase.getStoreId());
        inStore.setProductId(purchase.getProductId());
        inStore.setInNum(purchase.getFactBuyNum());
        inStore.setCreateBy(createBy);

        //执行业务
        Result result = inStoreService.saveInStore(inStore, purchase.getBuyId());

        //响应
        return result;
    }


    /*
     * 导出
     * */
    @RequestMapping("/exportTable")
    public Result exportTable(Page page, Purchase purchase){
        //分页查询仓库
        page = purchaseService.queryPurchasePage(page,purchase);
        //拿到当前页数据
        List<?> resultList = page.getResultList();
        //响应
        return Result.ok(resultList);
    }
}
