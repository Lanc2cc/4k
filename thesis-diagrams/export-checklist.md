<!--
 * @Author: R1cha2d 1103569372@qq.com
 * @Date: 2026-04-01 01:04:53
 * @LastEditors: R1cha2d 1103569372@qq.com
 * @LastEditTime: 2026-04-01 01:24:37
 * @FilePath: \4kmovie\thesis-diagrams\export-checklist.md
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
# Thesis Diagram Export Checklist

## 使用建议

- 论文正文优先使用精简版：thesis-diagrams/v2-compact
- 答辩PPT优先使用完整版：thesis-diagrams/v1-full

## 图号与文件映射

| 图号 | 图注建议 | 章节位置 | 精简版文件 | 完整版文件 | 论文推荐 |
| --- | --- | --- | --- | --- | --- |
| 图1-1 | 课题研究背景与问题定位示意图 | 第1章 1.1.2 后 | thesis-diagrams/v2-compact/fig1-1.mmd | thesis-diagrams/v1-full/fig1-1.mmd | 精简版 |
| 图1-2 | 系统建设目标与价值示意图 | 第1章 1.2.2 后 | thesis-diagrams/v2-compact/fig1-2.mmd | thesis-diagrams/v1-full/fig1-2.mmd | 精简版 |
| 图2-1 | 前端技术栈与模块关系图 | 第2章 2.1.1 后 | thesis-diagrams/v2-compact/fig2-1.mmd | thesis-diagrams/v1-full/fig2-1.mmd | 精简版 |
| 图3-1 | 用户端核心业务流程图 | 第3章 3.1.1 后 | thesis-diagrams/v2-compact/fig3-1.mmd | thesis-diagrams/v1-full/fig3-1.mmd | 精简版 |
| 图4-1 | 系统总体架构图 | 第4章 4.1.1 后 | thesis-diagrams/v2-compact/fig4-1.mmd | thesis-diagrams/v1-full/fig4-1.mmd | 精简版 |
| 图5-1 | 用户登录与权限控制实现流程图 | 第5章 5.1.1 后 | thesis-diagrams/v2-compact/fig5-1.mmd | thesis-diagrams/v1-full/fig5-1.mmd | 精简版 |
| 图6-1 | 系统后续迭代演进路线图 | 第6章 6.2.2 后 | thesis-diagrams/v2-compact/fig6-1.mmd | thesis-diagrams/v1-full/fig6-1.mmd | 精简版 |

## 导出步骤

1. 打开对应 .mmd 文件。
2. 使用 Mermaid 预览。
3. 导出为 PNG，建议宽度 1800-2400 像素。
4. 插入 Word 后设置为嵌入型。
5. 使用题注功能添加图号，不手打编号。
6. 最后统一更新图目录与目录页码。

## 最终核对

- 图号是否与论文正文一致。
- 图注是否与本清单一致。
- 图目录是否自动刷新。
- 第6章是否已插入图6-1。