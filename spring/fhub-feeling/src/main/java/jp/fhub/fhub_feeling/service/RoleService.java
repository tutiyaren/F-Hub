package jp.fhub.fhub_feeling.service;

import org.springframework.stereotype.Service;
import jp.fhub.fhub_feeling.repository.RoleRepository;
import jp.fhub.fhub_feeling.entity.Role;

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
}
