# AP-roove: Gestão de pacientes

Sistema de controle e registro de acesso na portaria do câmpus

## Guia de Comandos

Um guia de comandos para criar componentes para o frontend

### Módulos

Novo módulo

```
ng g m --routing <nome-do-módulo>
```

### Entidades

Entidades de dados

```
cd src/main/angular/src/app/entities
ng g class --skip-tests <nome-da-entidade> 
```

### Serviços

Serviços de acesso a APIs

```
cd src/main/angular/src/app/services
ng g service --skip-tests <nome-do-serviço> 
```

### Componentes complexos

Componentes complexos com separação entre controller e view

```
cd src/main/angular/src/app/components
ng g c --skip-tests -s <nome-do-componente> 
```

### Componentes simples

Componentes simples do frontend

```
cd src/main/angular/src/app/views
ng g c --skip-tests --flat -s -t <nome-do-componente>
```

## Executar em modo testes

Não esquecer da barra (/) final

```
ng build --watch --base-href /ap-roove/
```