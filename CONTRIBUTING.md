# 贡献指南

## Git 工作流

```
main  ←  develop  ←  feature/<name>
```

- **main**: 稳定发布分支，只接受来自 develop 的合并
- **develop**: 开发主分支，功能完成后合并至此
- **feature/<name>**: 个人功能开发分支，以成员名命名

## 分支命名规范

```
feature/<成员姓名拼音>
例：feature/shen-yuanqi
```

## Commit Message 规范

采用 Conventional Commits 格式：

```
<type>(<scope>): <简短描述>

- <详细变更说明>
- ...

Author: <姓名> <邮箱>
```

### Type 类型

| 类型 | 使用场景 |
|------|---------|
| feat | 新功能 |
| fix | Bug 修复 |
| docs | 文档变更 |
| refactor | 代码重构 |
| test | 测试相关 |
| chore | 构建/配置变更 |

### Scope 范围

| Scope | 模块 |
|-------|------|
| auth | 用户管理与认证 |
| text2image | 文生图 |
| image2text | 图生文 |
| ui | 前端界面 |
| style | 风格管理 |
| config | 系统配置 |
| inference | 推理服务 |
| model | 模型管理 |

## PR 流程

1. 从 develop 创建你的 feature 分支
2. 在分支上完成开发，按规范提交
3. 推送到远程仓库
4. 创建 Pull Request → develop
5. 至少 1 人 Review 通过后合并

## 代码风格

- Java: 遵循项目已有风格（Spring Boot 标准）
- Python: PEP 8
- Vue: 组件命名 PascalCase，文件命名保持一致
