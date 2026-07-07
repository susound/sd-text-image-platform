# Learnings

## 2026-07-07 — Git Branch Structure Setup

### Task
为 Nebula Studio 项目创建规范的 Git 分支结构。

### Actions Taken
1. **Created develop branch** from main HEAD (commit e272ee3)
2. **Created 3 feature branches** from develop:
   - eature/login — 用户登录/注册模块
   - eature/text2image — 文生图模块
   - eature/admin — 管理员模块
3. **Pushed all branches** to origin (GitHub: syq04/stable)

### Result
`
  develop
  feature/admin
  feature/login
  feature/text2image
  main
  remotes/origin/HEAD -> origin/main
  remotes/origin/develop
  remotes/origin/feature/admin
  remotes/origin/feature/login
  remotes/origin/feature/text2image
  remotes/origin/main
`

### Notes
- Branch naming follows Git Flow convention: develop as integration branch, eature/* for feature development
- SSL/TLS handshake was flaky — initial multi-push failed after develop; retried eature/* branches individually and succeeded
- All branches point to same commit (no divergence yet)

