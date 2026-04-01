# Tempo Amigo ⛅

Aplicativo Android que monitora condições climáticas em tempo real com base na localização do usuário, emitindo alertas automáticos em situações de risco.

> ⚠️ Projeto em desenvolvimento

## Funcionalidades

- Detecta automaticamente sua localização atual
- Monitora condições climáticas extremas em tempo real
- Envia notificações de alerta quando uma condição de risco é detectada
- Ao receber um alerta, pergunta se deseja avisar um contato de confiança
- Redireciona para o WhatsApp com mensagem já pronta, incluindo a condição detectada e sua localização

## Condições monitoradas

| Condição | Limite |
|---|---|
| Calor extremo | Acima de 35°C |
| Frio extremo | Abaixo de 10°C |
| Umidade alta | Acima de 95% |
| Umidade baixa | Abaixo de 20% |
| Vento | Acima de 60 km/h |
| Chuva | Acima de 50 mm |
| Probabilidade de chuva | Acima de 90% |

## Tecnologias

- Java (Android SDK 31+)
- [Open-Meteo API](https://open-meteo.com/) — dados climáticos

## Requisitos

- Android 12 ou superior
- Permissão de localização
- Conexão com a internet