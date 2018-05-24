package com.xinliangjishipin.pushwms.controller;

import com.github.pagehelper.PageInfo;
import com.xinliangjishipin.pushwms.config.WebSecurityConfig;
import com.xinliangjishipin.pushwms.dto.CustomerInfoDto;
import com.xinliangjishipin.pushwms.entity.AlipayBillExtend;
import com.xinliangjishipin.pushwms.entity.CustomerInfo;
import com.xinliangjishipin.pushwms.entity.ResultResponse;
import com.xinliangjishipin.pushwms.mapper.CustomerInfoMapper;
import com.xinliangjishipin.pushwms.service.DownloadAlipayBillService;
import com.xinliangjishipin.pushwms.service.ManualAlipayBillService;
import com.xinliangjishipin.pushwms.service.MatchAlipayAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
public class ManualBillController {

    @Autowired
    private ManualAlipayBillService manualAlipayBillService;

    @Autowired
    private DownloadAlipayBillService downloadAlipayBillService;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;


    @GetMapping("/md/{downloadDay}")
    @ResponseBody
    public String manualDownLoadAlipayBillList(@PathVariable String downloadDay) {
        downloadAlipayBillService.downloadAlipayByDate(downloadDay);
        return downloadDay + "日支付宝账单下载完成！";
    }

    @GetMapping("/list")
    public String manualAlipayBillList(ModelMap map,
                                       @RequestParam(required = false) String businessNo,
                                       @RequestParam(required = false) String startTime,
                                       @RequestParam(required = false) String endTime,
                                       @RequestParam(required = false) String importType,
                                       @RequestParam(required = false) String matchStatus,
                                       @RequestParam(required = false) String verifyStatus,
                                       @RequestParam(required = false) String responseStatus,
                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {

        PageInfo<AlipayBillExtend> pageInfo = manualAlipayBillService.manualAlipayAccount(businessNo, startTime, endTime,
                importType, matchStatus, verifyStatus, responseStatus, pageNum, pageSize);

        map.addAttribute("billList", pageInfo.getList());
        //获得当前页
        map.addAttribute("pageNum", pageInfo.getPageNum());
        //获得一页显示的条数
        map.addAttribute("pageSize", pageInfo.getPageSize());
        //是否是第一页
        map.addAttribute("isFirstPage", pageInfo.isIsFirstPage());
        //获得总页数
        map.addAttribute("totalPages", pageInfo.getPages());
        //是否是最后一页
        map.addAttribute("isLastPage", pageInfo.isIsLastPage());
        map.addAttribute("businessNo", businessNo == null ? "" : businessNo);
        map.addAttribute("startTime", startTime == null ? "" : startTime);
        map.addAttribute("endTime", endTime == null ? "" : endTime);
        map.addAttribute("importType", importType == null ? "" : importType);
        map.addAttribute("matchStatus", matchStatus == null ? "" : matchStatus);
        map.addAttribute("verifyStatus", verifyStatus == null ? "" : verifyStatus);
        map.addAttribute("responseStatus", responseStatus == null ? "" : responseStatus);

        return "billList";
    }

    @GetMapping("/add-bill")
    public String manualAddBill() {
        return "addBill";
    }

    @PostMapping("/add-bill")
    @ResponseBody
    public ResultResponse manualPostBill(@ModelAttribute AlipayBillExtend alipayBillExtend, HttpSession session) {
        alipayBillExtend.setCreatedUser(session.getAttribute(WebSecurityConfig.REALNAME_KEY).toString());
        alipayBillExtend.setCreatedTime(new Date());
        return manualAlipayBillService.matchResult(alipayBillExtend);
    }

    @GetMapping("/customer/{businessNo}")
    @ResponseBody
    public List<CustomerInfoDto> getCustomerInfoByBusinessNo(@PathVariable String businessNo) {
        String strCustomerInfo = manualAlipayBillService.getByBusinessNo(businessNo) != null ? manualAlipayBillService.getByBusinessNo(businessNo).getDef1() : "";
        if(!StringUtils.isEmpty(strCustomerInfo)){
            return manualAlipayBillService.convertStringToCustomerInfo(strCustomerInfo);
        }
        return null;
    }

    @RequestMapping(value = "/customer/manual", method = RequestMethod.POST)
    public String manualSummit(@RequestParam("selectBusinessNo") String selectBusinessNo, @RequestParam("customerCode") String customerCode, HttpSession session){
        String updateUser = session.getAttribute(WebSecurityConfig.REALNAME_KEY).toString();
        manualAlipayBillService.manualUpdateAlipayBillExtend(selectBusinessNo, customerCode, updateUser);
        return "redirect:/list";
    }
}
