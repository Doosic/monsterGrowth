package com.fc_study.monsterGrowth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fc_study.monsterGrowth.code.StatusCode;
import com.fc_study.monsterGrowth.dto.CreateMonsterDto;
import com.fc_study.monsterGrowth.dto.DetailMonsterDto;
import com.fc_study.monsterGrowth.dto.UpdateMonsterDto;
import com.fc_study.monsterGrowth.entity.MonsterEntity;
import com.fc_study.monsterGrowth.repository.MonsterRepository;
import com.fc_study.monsterGrowth.service.MMakerService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fc_study.monsterGrowth.code.StatusCode.*;
import static com.fc_study.monsterGrowth.entity.MonsterEntity.MonsterLevel.ADULT;
import static com.fc_study.monsterGrowth.entity.MonsterEntity.MonsterLevel.BABY;
import static com.fc_study.monsterGrowth.entity.MonsterEntity.MonsterType.FLY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(MMakerController.class)
class MMakerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MMakerService mMakerService;

    private MonsterEntity getDefaultMonster(Long id, String ssn, String name, StatusCode statusCode){
        return MonsterEntity.builder()
            .id(id)
            .monsterLevel(BABY)
            .monsterType(FLY)
            .statusCode(statusCode)
            .ssn(ssn)
            .name(name)
            .age(3)
            .height(170)
            .weight(73)
            .build();
    }
    private CreateMonsterDto.Request getCreateRequest() {
        return CreateMonsterDto.Request.builder()
                .id(1L)
                .monsterLevel(BABY)
                .monsterType(FLY)
                .statusCode(HEALTHY)
                .ssn("12345612345123")
                .name("BabyMonster")
                .age(3)
                .height(170)
                .weight(73)
                .build();
    }
    private UpdateMonsterDto.Request getUpdateMonster(){
        return UpdateMonsterDto.Request.builder()
                .monsterLevel(BABY)
                .monsterType(FLY)
                .statusCode(SICK)
                .ssn("96050312341234")
                .age(3)
                .height(170)
                .weight(73)
                .build();
    }


    /*
          ??????????????? ????????? ?????????.
     */
    protected MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8);

    /*
    * ????????? ?????? ????????? ???
    * then().should(times()) ??? times??? ????????? when ?????? ?????? ????????? ???????????? ???????????? ??????
    * ?????? ??????????????? ????????? ??????????????? ????????????.
    * */
    @Test
    @DisplayName("Monster get Test")
    void getDetailMonster() throws Exception{
        // given
        MonsterEntity defaultMonster = getDefaultMonster(1L, "12345612345123", "Tiger", HEALTHY);
        given(mMakerService.getDetailMonster(any()))
                .willReturn(DetailMonsterDto.fromEntity(getDefaultMonster(1L, "12345612345123", "Tiger", HEALTHY)));

        // when
        // then
        mockMvc.perform(
                get("/detail-monster/"+defaultMonster.getSsn())
                        .contentType(contentType)
                        .content(defaultMonster.getSsn()))
                .andExpect(status().isOk())
                .andDo(print());
        then(mMakerService).should(times(1)).getDetailMonster(defaultMonster.getSsn());
    }

    @Test
    @DisplayName("Monster allList Test")
    void getAllList() throws Exception{
        // given
        List<MonsterEntity> monsterList  = new ArrayList<>();
        monsterList.add(getDefaultMonster(1L, "First Monster", "96050312341231", HEALTHY));
        monsterList.add(getDefaultMonster(2L, "Second Monster", "96050312341232", HEALTHY));
        monsterList.add(getDefaultMonster(3L, "Third Monster", "96050312341233", HEALTHY));

        given(mMakerService.getAllDetailMonster())
                .willReturn(monsterList.stream()
                        .map(DetailMonsterDto::fromEntity)
                        .collect(Collectors.toList())
                );

        // when
        // then
        mockMvc.perform(
                get("/all-monster")
                        .contentType(contentType))
                .andExpect(status().isOk())
                .andDo(print());
        then(mMakerService).should(times(1)).getAllDetailMonster();
    }

    @Test
    @DisplayName("Monster Created Test")
    void createMonster() throws Exception {
        // given: ????????? ???????????? ??????????????? ???, ?????? ???????????? ???????????? ?????? ?????? Return ??? ??? ??? ??????
        //        ????????? ????????? ?????? ?????? ????????? ???????????????.
        MonsterEntity defaultMonster = getDefaultMonster(1L, "96050312341234", "Tiger", HEALTHY);
        given(mMakerService.createMonster(getCreateRequest()))
                .willReturn(CreateMonsterDto.TestResponse.fromEntity(defaultMonster));

        // when: ????????? ????????? ????????????
        // then: ????????? ????????? ????????? ??????.
        mockMvc.perform(
                        post("/create-monster")
                                .contentType(contentType)
                                .content(
                                        new ObjectMapper().writeValueAsString(
                                                CreateMonsterDto.TestResponse.fromEntity(defaultMonster)
                                        )))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        then(mMakerService).should(times(1)).createMonster(getCreateRequest());
    }

    @Test
    @DisplayName("Monster updated Test")
    void updateMonster() throws Exception {
        // given
        MonsterEntity updateMonster = getDefaultMonster(1L, "96050312341234","updateMonster", HEALTHY);

        given(mMakerService.updateMonster(anyString(), any()))
                .willReturn(DetailMonsterDto.fromEntity(updateMonster));

        // when
        // then
        mockMvc.perform(
                put("/update-monster")
                        .contentType(contentType)
                        .content(
                                new ObjectMapper().writeValueAsString(
                                getUpdateMonster()
                        )))
                .andExpect(status().isOk())
                .andDo(print());
        then(mMakerService).should(times(1)).updateMonster(anyString(),any());
    }

    @Test
    @DisplayName("Monster delete Test")
    void deleteMonster() throws Exception{
        // given
        MonsterEntity deleteMonster = getDefaultMonster(1L,"96050312341234", "deleteMonster", DEAD);
        given(mMakerService.deleteMonster(anyString()))
                .willReturn(
                        DetailMonsterDto.fromEntity(deleteMonster)
                );

        // when
        // then
        mockMvc.perform(
                delete("/delete-monster/"+deleteMonster.getSsn())
                        .contentType(contentType)
                        .content(deleteMonster.getSsn())
                )
                .andExpect(status().isOk())
                .andDo(print());
        then(mMakerService).should(times(1)).deleteMonster(deleteMonster.getSsn());

    }

}