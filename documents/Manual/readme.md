# 📝 Manual
Manual de instalação do projeto Sphera da equipe Minerva
## Dependências
> Java JDK 21 <br>
 Maven <br>
MySQL 8.4 <br>
JavaFX <br>

**1. Clone o repósitrório:**
```bash
git clone https://github.com/Minerva2025/Minerva.git
```
**2. Configuração do banco de dados através do arquivo [schema.sql](/src/main/resources/schema.sql)**
> Depois de criar o banco, configure pelo [ConnectionFactory.java](/src/main/java/ConnectionFactory.java)

**3. Rode o programa com o Maven**
```bash
mvn javafx:run
```
