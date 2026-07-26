# TOS Mod

Mod de computadores modulares pra NeoForge 1.21.1 - veja `STATUS.md` pro resumo completo
de tudo que já foi construído (10 fases + assets integrados).

## Como compilar via GitHub (sem precisar instalar nada no celular)

1. Crie um repositório novo no GitHub (pode ser privado).
2. Suba **todo o conteúdo desta pasta** pra ele. Duas formas de fazer isso:
   - **Pelo site do GitHub** (mais fácil, sem terminal): entre no repositório vazio,
     clique em "uploading an existing file", arraste todos os arquivos/pastas
     (`src`, `.github`, `build.gradle`, `settings.gradle`, `gradle.properties`,
     `.gitignore`, `STATUS.md`, este `README.md`) e faça o commit.
   - **Por linha de comando** (se tiver um PC com git instalado):
     ```
     git init
     git add .
     git commit -m "primeira versão do TOS mod"
     git remote add origin <URL do seu repositório>
     git push -u origin main
     ```
3. Assim que o push terminar, vá na aba **Actions** do repositório no GitHub. Um workflow
   chamado "Build TOS Mod" já deve estar rodando (ou rodando em alguns segundos).
4. Quando ele terminar (ícone verde ✅), clique nele, desça até **Artifacts**, e baixe
   `tosmod-jar` - é o `.jar` do mod já compilado, pronto pra colocar na pasta `mods` do
   Minecraft.

Isso roda automaticamente TODA VEZ que você der push de novo (ex: depois de adicionar
receitas, mais modelos, etc.) - não precisa configurar nada de novo.

## Pendência técnica: Gradle Wrapper

Este projeto ainda **não tem o Gradle Wrapper** (`gradlew`/`gradlew.bat`) gerado - são
arquivos que normalmente vêm junto num projeto Gradle, mas que eu não consigo gerar aqui
porque preciso baixar um `.jar` da internet pra isso, e não tenho acesso à rede neste
ambiente.

Isso **não afeta a compilação pelo GitHub Actions** (o workflow já foi ajustado pra
instalar o Gradle direto, sem depender do wrapper). Só afeta se um dia você (ou alguém)
quiser compilar **localmente**, direto no PC, fora do GitHub. Se isso acontecer, a
correção é rápida - com o Gradle instalado localmente, rode uma vez:
```
gradle wrapper --gradle-version 8.13
```
Isso gera os arquivos que faltam, e o projeto passa a funcionar com `./gradlew` também,
localmente.

## Testar o mod de verdade

Depois de baixar o `.jar` compilado, jogar com ele é o próximo passo — mas lembrando do
seu ambiente (PojavLauncher/Mojo + gl4es no Android): teste primeiro com poucos blocos
por vez, igual fizemos com o Sable, pra isolar qualquer problema de renderização cedo.
