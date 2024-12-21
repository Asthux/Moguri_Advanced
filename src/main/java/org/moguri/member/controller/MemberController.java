package org.moguri.member.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.moguri.common.enums.ReturnCode;
import org.moguri.common.response.ApiResponse;
import org.moguri.common.response.MoguriPage;
import org.moguri.common.response.PageRequest;
import org.moguri.common.validator.PageLimitSizeValidator;
import org.moguri.member.domain.Member;
import org.moguri.member.param.MemberCreateParam;
import org.moguri.member.param.MemberUpdateParam;
import org.moguri.member.service.MemberService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ApiResponse<?> getMembers(MemberGetRequest request) {
        PageLimitSizeValidator.validateSize(request.getPage(), request.getLimit(), 100);
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getLimit());

        List<Member> members = memberService.getMembers(pageRequest);
        int totalCount = memberService.getTotalCount();

        return ApiResponse.of(MoguriPage.of(pageRequest, totalCount,
                members.stream().map(MemberItem::of).toList()));
    }

    @PostMapping
    public ApiResponse<?> create(@RequestBody MemberCreateRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        MemberCreateParam param = request.convert(passwordEncoder);
        memberService.save(param);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getMember(@PathVariable("id") Long id) {
        Member member = memberService.getMember(id);
        return ApiResponse.of(MemberItem.of(member));
    }

    @PatchMapping("/{id}")
    public ApiResponse<?> update(@PathVariable("id") Long id, @RequestBody MemberUpdateRequest request) {
        MemberUpdateParam param = request.convert();
        memberService.update(param);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable("id") Long id) {
        memberService.remove(id);
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    @PatchMapping("/{id}/cotton-candy")
    public ApiResponse<?> updateCottonCandy(@PathVariable("id") Long id, @RequestBody CottonCandyUpdateRequest request) {
        memberService.updateCottonCandy(id, request.getCottonCandy());
        return ApiResponse.of(ReturnCode.SUCCESS);
    }

    @GetMapping("/{id}/cotton-candy")
    public ApiResponse<?> getCottonCandy(@PathVariable("id") Long id) {
        int cottonCandy = memberService.getCottonCandy(id);
        return ApiResponse.of(cottonCandy);
    }


    @Data
    private static class MemberGetRequest {
        private int page = 0;
        private int limit = 30; // default 값
    }

    @Data
    private static class MemberItem {
        private String email; // id
        private String password;
        private String nickName;

        private static MemberItem of(Member member) {
            MemberItem converted = new MemberItem();
            converted.email = member.getEmail();
            converted.password = member.getPassword();
            converted.nickName = member.getNickName();
            return converted;
        }
    }

    @Data
    private static class MemberCreateRequest {
        private String email; // id
        private String password;
        private String nickName;

        public MemberCreateParam convert(PasswordEncoder passwordEncoder) {
            return MemberCreateParam.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .nickName(nickName)
                    .build();
        }
    }

    @Data
    private static class MemberUpdateRequest {
        private String password;
        private String nickName;

        public MemberUpdateParam convert() {
            return MemberUpdateParam.builder()
                    .password(password)
                    .nickName(nickName)
                    .build();
        }
    }

    // CottonCandyUpdateRequest 데이터 클래스 추가
    @Data
    private static class CottonCandyUpdateRequest {
        private int cottonCandy; // 업데이트할 코튼 캔디 수량
    }
}
