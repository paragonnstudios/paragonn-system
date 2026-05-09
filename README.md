# 🚀 Paragonn System

**Sistema geral completo, otimizado e extremamente útil para todos os tipos de servidores Minecraft.**

## Versão e release (GitHub)

Fluxo alinhado ao **paragonn-core**: commits em **`main`** ou **`master`** com [Conventional Commits](https://www.conventionalcommits.org/) → [Release Please](https://github.com/googleapis/release-please) abre uma **Release PR** → ao **merge**, são criados **tag** (`vX.Y.Z`), **GitHub Release** e o workflow **`.github/workflows/release.yml`** publica o asset **`paragonn-system.jar`**.

- **Versão no JAR:** no CI passa-se **`-PreleaseVersion`** a partir da tag (`v1.12.3` → `1.12.3`). Localmente: **`.release-please-manifest.json`** ou `./gradlew jar -PreleaseVersion=1.12.3` ou env **`RELEASE_VERSION`** / **`GITHUB_REF`** (ver `build.gradle`).
- **Token opcional:** secret **`RELEASE_PLEASE_TOKEN`** (PAT com `contents` e `pull-requests`) se o `GITHUB_TOKEN` padrão não for suficiente para abrir/atualizar a PR de release ou anexar o JAR.

O Paragonn System é a evolução de um dos plugins Essentials brasileiros mais utilizados. Construído ao longo de mais de 5 anos de desenvolvimento contínuo, ele oferece um conjunto completo de funcionalidades para simplificar a gestão de servidores, melhorar a performance e elevar a experiência dos jogadores.

---

## 📌 Sobre o Projeto

Criado originalmente por **RUSHyotuber**, o projeto cresceu com a ajuda de uma forte comunidade de desenvolvedores e contribuidores.

Hoje, o Paragonn System se posiciona como uma solução **madura, estável e amplamente testada em produção**, utilizada por servidores de diversos portes.

> ⚠️ **Status de Manutenção**
> Desde a versão `1.14.19` (lançada em 23 de junho de 2022), o projeto não recebe atualizações ativas.
> Ainda assim, permanece **100% estável**, amplamente utilizado e funcional.

---

## ⚙️ Compatibilidade

* ✅ Versões do Minecraft: **1.5 até 1.19**
* ⚠️ Suporte experimental: **1.20 / 1.21**
* 💡 Projetado para alta performance e estabilidade em diferentes escalas de servidores

---

## ✨ Principais Funcionalidades

* Sistema completo de Essentials
* Comandos administrativos e utilitários
* Estrutura modular para personalização
* Alto desempenho mesmo em servidores grandes
* Base sólida e confiável em produção

---

## 🧠 Melhorias Futuras (Guiadas pela Comunidade)

Caso alguém queira dar continuidade ao projeto, estas são algumas melhorias recomendadas:

* [ ] Adicionar suporte ao sistema de Kits para versões **1.17+**
* [ ] Implementar suporte a cores RGB nas versões mais recentes
* [ ] Atualizar o comando `/bigorna` para versões **1.13+**
* [ ] Melhorar `/compactar` e `/derreter` para novos minérios
* [ ] Aprimorar `/estatisticas` e `/verinfo` com dados mais recentes
* [ ] Estudar novas funcionalidades das versões pós-1.13
* [ ] Analisar necessidades atuais de servidores modernos
* [ ] Otimizar desempenho com base em timings de grandes servidores

---

## 🤝 Contribuição

Atualmente, o projeto não possui um mantenedor ativo.

Se você deseja continuar o desenvolvimento:

* Abra um Pull Request
* Ou entre em contato com o criador original

Toda contribuição é bem-vinda.

---

## 👥 Créditos

Este projeto só foi possível graças às contribuições de:

**RUSHyotuber (Criador)**

AnonyDev, LeoDev, Wolf_131, leonardosc, TequilAxBr, zAth, Jota, KickPost, gcunha, Gutyerrez, AlexHackers, BigWriter, Hard, Alomax, Joao Seidel, Pica-Pau, codename_G, Shisui, Kaway, Jamp, dargoh, aureom, VitorBlog, NatanDev, Duck, DvH, MarcioRUSH

---

## 🔗 Links Oficiais

* Spigot: [https://www.spigotmc.org/resources/system.102876/](https://www.spigotmc.org/resources/system.102876/)
* GamersBoard: [https://gamersboard.com.br/topic/61255-system-o-seu-novo-super-essentials](https://gamersboard.com.br/topic/61255-system-o-seu-novo-super-essentials)

---

## 💬 Considerações Finais

O Paragonn System não é apenas um plugin — é o resultado de anos de dedicação, aprendizado e colaboração.

Mesmo sem atualizações recentes, continua sendo uma solução extremamente confiável.

Se alguém decidir levá-lo adiante, há uma base sólida pronta para evoluir ainda mais.