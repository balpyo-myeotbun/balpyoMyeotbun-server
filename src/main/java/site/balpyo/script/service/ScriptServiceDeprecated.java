package site.balpyo.script.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import site.balpyo.ai.entity.AIGenerateLogEntity;
import site.balpyo.ai.entity.GPTInfoEntity;
import site.balpyo.ai.repository.AIGenerateLogRepository;
import site.balpyo.ai.repository.GPTInfoRepository;
import site.balpyo.common.dto.CommonResponse;
import site.balpyo.common.dto.ErrorEnum;
import site.balpyo.guest.entity.GuestEntity;
import site.balpyo.guest.repository.GuestRepository;
import site.balpyo.script.dto.ScriptRequest;
import site.balpyo.script.dto.ScriptResponse;
import site.balpyo.script.entity.ScriptEntity;
import site.balpyo.script.repository.ScriptRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScriptServiceDeprecated {

    private final ScriptRepository scriptRepository;
    private final GuestRepository guestRepository;
    private final AIGenerateLogRepository aiGenerateLogRepository;
    private final GPTInfoRepository gptInfoRepository;


    public ResponseEntity<CommonResponse> saveScript(ScriptRequest scriptRequest, String uid) {

        GuestEntity guestEntity = null;
        if (uid != null) {
            guestEntity = guestRepository.findById(uid).orElse(null);
        }

        GPTInfoEntity gptInfoEntity = null;
        if (scriptRequest.getGptId() != null) {
            gptInfoEntity = gptInfoRepository.findById(scriptRequest.getGptId()).orElse(null);
        }

        AIGenerateLogEntity aiGenerateLogEntity = null;
        if (gptInfoEntity != null) {
            Optional<AIGenerateLogEntity> aiGenerateLogEntityOptional = aiGenerateLogRepository.findByGptInfoEntity(gptInfoEntity);
            aiGenerateLogEntity = aiGenerateLogEntityOptional.orElse(null);
            System.out.println(aiGenerateLogEntity.getTopic());
        }

        ScriptEntity scriptEntity = ScriptEntity.builder()
                .title(scriptRequest.getTitle())
                .script(scriptRequest.getScript())
                .secTime(scriptRequest.getSecTime())
                .guestEntity(guestEntity)
                .aiGenerateLogEntity(aiGenerateLogEntity)
                .voiceFilePath(scriptRequest.getVoiceFilePath())
                .build();

        scriptRepository.save(scriptEntity);

        ScriptResponse scriptResponse = new ScriptResponse(null,scriptRequest.getScript(), scriptRequest.getGptId(),uid,scriptRequest.getTitle(),scriptRequest.getSecTime(),null);

        return CommonResponse.success(scriptResponse);
    }

    public ResponseEntity<CommonResponse> getAllScript(String uid) {
        Optional<GuestEntity> guestEntity = guestRepository.findById(uid);

        if(guestEntity.isEmpty())return CommonResponse.error(ErrorEnum.GUEST_NOT_FOUND);
        List<ScriptResponse> scriptResponses = new ArrayList<>();
        if(guestEntity.get().getScriptEntities().isEmpty())return CommonResponse.success(scriptResponses);

        List<ScriptEntity> scriptEntities = guestEntity.get().getScriptEntities();


        for(ScriptEntity scriptEntity: scriptEntities){
              ScriptResponse scriptResponse = ScriptResponse.builder()
                .scriptId(scriptEntity.getScript_id())
                .uid(uid)
                .title(scriptEntity.getTitle())
                .secTime(scriptEntity.getSecTime())
                      .voiceFilePath(scriptEntity.getVoiceFilePath())
                .build();

        scriptResponses.add(scriptResponse);
    }

        return CommonResponse.success(scriptResponses);

    }

    public ResponseEntity<CommonResponse> getDetailScript(String uid, Long scriptId) {
        Optional<ScriptEntity> optionalScriptEntity = scriptRepository.findScriptByGuestUidAndScriptId(uid,scriptId);

        if(optionalScriptEntity.isEmpty())return CommonResponse.error(ErrorEnum.SCRIPT_DETAIL_NOT_FOUND);

        ScriptEntity scriptEntity = optionalScriptEntity.get();

        ScriptResponse scriptResponse = ScriptResponse
                .builder()
                .scriptId(scriptEntity.getScript_id())
                .secTime(scriptEntity.getSecTime())
                .uid(scriptEntity.getGuestEntity().getUid())
                .title(scriptEntity.getTitle())
                .script(scriptEntity.getScript())
                .voiceFilePath(scriptEntity.getVoiceFilePath())
                .build();

        return CommonResponse.success(scriptResponse);


    }
    public ResponseEntity<CommonResponse> patchScript(ScriptRequest scriptRequest, String uid,Long scriptId) {
        Optional<ScriptEntity> optionalScriptEntity = scriptRepository.findScriptByGuestUidAndScriptId(uid, scriptId);

        if(optionalScriptEntity.isEmpty())return CommonResponse.error(ErrorEnum.SCRIPT_DETAIL_NOT_FOUND);

        ScriptEntity scriptEntity = optionalScriptEntity.get();


        scriptEntity.setScript(scriptRequest.getScript());
        scriptEntity.setTitle(scriptRequest.getTitle());
        scriptEntity.setSecTime(scriptRequest.getSecTime());



        if (scriptRequest.getVoiceFilePath() != null && !scriptRequest.getVoiceFilePath().isEmpty()) {
            scriptEntity.setVoiceFilePath(scriptRequest.getVoiceFilePath());
        }

        scriptRepository.save(scriptEntity);

        return CommonResponse.success("");


    }


    public ResponseEntity<CommonResponse> deleteScript(String uid, Long scriptId) {
        Optional<ScriptEntity> optionalScriptEntity = scriptRepository.findScriptByGuestUidAndScriptId(uid, scriptId);

        if(optionalScriptEntity.isEmpty())return CommonResponse.error(ErrorEnum.SCRIPT_DETAIL_NOT_FOUND);

        ScriptEntity scriptEntity = optionalScriptEntity.get();

        scriptRepository.delete(scriptEntity);

        return CommonResponse.success("");

    }
}
