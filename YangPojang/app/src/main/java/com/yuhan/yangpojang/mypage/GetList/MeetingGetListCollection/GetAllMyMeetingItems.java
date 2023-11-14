package com.yuhan.yangpojang.mypage.GetList.MeetingGetListCollection;

import android.util.Log;

import com.yuhan.yangpojang.fragment.ProfileShowFragment;
import com.yuhan.yangpojang.model.Shop;
import com.yuhan.yangpojang.mypage.Adapter.MyMeetingAdapter;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.AllMeetingItemModel;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.MeetingAttendersModel;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.MeetingModel;
import com.yuhan.yangpojang.mypage.Model.MeetingModelCollection.MyMeetingModel;

import java.util.ArrayList;

public class GetAllMyMeetingItems {

    ArrayList<MyMeetingModel> myMeeting = new ArrayList<>();
    ArrayList<MeetingModel> meetingList = new ArrayList<>();
    ArrayList<Shop> shopArrayList = new ArrayList<>();
    ArrayList<ArrayList<MeetingAttendersModel>> attender = new ArrayList<>();
    ArrayList<AllMeetingItemModel> allMeetingItemModels = new ArrayList<>();
    ArrayList<AllMeetingItemModel> tempMeetingItemModels = new ArrayList<>();

    public void getMeetingInfo(final allMeetingItemLoadCallback callback){
        MyMeetingGetList.myMeetingDataLoad(new MyMeetingGetList.myMeetingDataLoadedCallback() {
            @Override
            public void onMyMeetingLoaded(ArrayList<MyMeetingModel> myMeetings) {
                myMeeting.clear();
                myMeeting.addAll(myMeetings); //번개ID : 가게ID
                MeetingGetList.getMeetingId_ShopId(myMeeting);
                MeetingGetList.meetingDataLoad(new MeetingGetList.meetingDataLoadedCallback() {
                    @Override
                    public void onMeetingLoaded(ArrayList<MeetingModel> meetings) {
                        meetingList.clear();
                        meetingList.addAll(meetings); //date~yeardate
                        ShopGetList.getShopId(myMeeting);
                        ShopGetList.meetingDataLoad(new ShopGetList.shopDataLoadedCallback() {
                            @Override
                            public void onShopLoaded(ArrayList<Shop> shops) {
                                shopArrayList.clear();
                                shopArrayList.addAll(shops);
                                MeetingAttendersGetList.getMeetingId(myMeeting);
                                MeetingAttendersGetList.attendersDataLoad(new MeetingAttendersGetList.attenderDataLoadedCallback() {
                                    @Override
                                    public void onAttenderLoaded(ArrayList<ArrayList<MeetingAttendersModel>> attenders) {
                                        attender.clear();
                                        attender.addAll(attenders);
                                        tempMeetingItemModels.clear();
                                        // 합치기
                                        for(int i = 0; i < myMeetings.size(); i++) {
                                            AllMeetingItemModel allMeetingItemModel = new AllMeetingItemModel();
                                            allMeetingItemModel.setMeetingId(myMeetings.get(i).getMeetingId());
                                            allMeetingItemModel.setShopId(myMeetings.get(i).getShopId());
                                            allMeetingItemModel.setShop(shopArrayList.get(i));
                                            allMeetingItemModel.setTitle(meetingList.get(i).getTitle());
                                            allMeetingItemModel.setTime(meetingList.get(i).getTime());
                                            allMeetingItemModel.setMaxAge(meetingList.get(i).getMaxAge());
                                            allMeetingItemModel.setMinAge(meetingList.get(i).getMinAge());
                                            allMeetingItemModel.setMaxMember(meetingList.get(i).getMaxMember());
                                            allMeetingItemModel.setCountAttenders(attender.get(i).size());
                                            allMeetingItemModel.setAddressName(shopArrayList.get(i).getAddressName());
                                            allMeetingItemModel.setAttenders(attender.get(i));
                                            tempMeetingItemModels.add(allMeetingItemModel);
                                        }
                                        allMeetingItemModels.clear();
                                        allMeetingItemModels.addAll(tempMeetingItemModels);
                                        callback.onAllLoaded(allMeetingItemModels);
                                        Log.d("ㅈ", "데이터로드 : " + allMeetingItemModels);
                                        Log.d("ㅈ", "데이터로드(size) : " + allMeetingItemModels.size());

                                    }
                                });


                            }
                        });
                    }
                });
            }
        });
    }

    public interface allMeetingItemLoadCallback{
        void onAllLoaded(ArrayList<AllMeetingItemModel> allMeetingItemModels);
    }

}
