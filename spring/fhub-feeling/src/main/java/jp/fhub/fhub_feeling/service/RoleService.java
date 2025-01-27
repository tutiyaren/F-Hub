package jp.fhub.fhub_feeling.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import jp.fhub.fhub_feeling.repository.RoleRepository;
import jp.fhub.fhub_feeling.entity.Hospital;
import jp.fhub.fhub_feeling.entity.HospitalUser;
import jp.fhub.fhub_feeling.entity.Role;
import jp.fhub.fhub_feeling.entity.User;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("会員登録に失敗しました。詳細は管理者にお問い合わせください。"));
    }
    
    public boolean isUserRole(String roleName) {
        return "user".equals(roleName);
    }

    public boolean isHospitalAdminRole(String roleName) {
        return "hospital_admin".equals(roleName);
    }

    public boolean isSystemAdminRole(String roleName) {
        return "system_admin".equals(roleName);
    }

    public List<UUID> getHospitalIds(User user) {
        return user.getHospitalUsers().stream()
                .map(HospitalUser::getHospital)
                .filter(Objects::nonNull)
                .map(Hospital::getId)
                .distinct()
                .toList();
    }
}
