# Грузовые перевозки
Моделирование грузоперевозок на мульти-агентах. 

## Постановка задачи
Задача мультиагентного планирования контейнерных грузоперевозок с промежуточными складами.

Существует 20 логистических пунктов (пунктов назначения). Интенсивность поступления заказов 1 или 2 заказа каждые 5 минут. Для реализации транспортировки заказов используется парк, состоящий из 10 транспортных средств. При выполнении заказов возможна перегрузка товаров между грузовиками и обмен прицепами. В момент генерации заказа определяется «дальность» доставки (от 1 до 3-х пунктов удаленности, соответствует временным затратам на доставку от 1 до 3 часов).

Время смены 12 часов. Время погрузки/перегрузки/разгрузки/замены прицепа 0,5 часа. Один контейнер может вмещать груз от 1 до 2-ух заказов. Контейнеровоз может перевозить от 2-ух до 3-ех контейнеров одновременно. При выполнении поставки, грузовик за один рейс в среднем посещает до 3-х пунктов.

Разработать модель работы логистической сети и оценить несколько вариантов её работы в течение одной смены по параметрам.

## Параметры модели для оценки
1) прибыль компании в условных единицах;
1) среднее количество задействованных грузовиков;
1) количество замен прицепами;
1) количество перегрузок;
1) распределение количества прицепов и грузовиков по пунктам.

## Варианты модельных ситуаций
1) жёсткое планирование (план составляется один раз и не предусматривает корректировки);
1) составляется первичный план, который может быть пересмотрен согласно концепции «мультиагентного планирования» на любом шаге (что позволяет оперативно пользоваться заменой прицепов и перегрузками в зависимости от текущей ситуации).

## Исходные данные для целевой функции прибыли
Для заказчика стоимость транспортировки 1 заказа по одному плечу 1 условная единица, затраты на перевозку составляют 0,5 условной единицы. Порожняя перевозка (затраты) по одному плечу составляет 0,2 условной единицы. Стоимость 1 операции поста погрузки/разгрузки/перегрузки/замены прицепа 0,1 условной единицы.

## Метаданные
УрФУ, ИРИТ-РТФ, Школа профессионального и академического образования, 2019.