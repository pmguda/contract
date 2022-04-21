--상품 정보 생성
insert into product (prod_cd, prod_nm, min_prd, max_prd)
values('P0001', '여행자보험', 1, 3);
insert into product (prod_cd, prod_nm, min_prd, max_prd)
values('P0002','휴대폰보험', 1, 12);

--담보 정보 생성
insert into guarantee (grnt_cd, prod_cd, grnt_nm, join_amt, base_amt)
values('I000101','P0001','상해치료비',1000000,100);
insert into guarantee (grnt_cd, prod_cd, grnt_nm, join_amt, base_amt)
values('I000102','P0001','항공기 지연도착시 보상금',500000,100);
insert into guarantee (grnt_cd, prod_cd, grnt_nm, join_amt, base_amt)
values('I000201','P0002','부분손실',750000,38);
insert into guarantee (grnt_cd, prod_cd, grnt_nm, join_amt, base_amt)
values('I000202','P0002','전체손실',1570000,40);

--계약 정보 생성
insert into contract (poli_no,prod_cd,cntr_prd,cntr_scd,tot_prm,cntr_dt,ex_dt)
values('test1','P0002',3,'0',176960.52,'20220127','20220426');
insert into contract (poli_no,prod_cd,cntr_prd,cntr_scd,tot_prm,cntr_dt,ex_dt)
values('test2','P0002',3,'0',59210.52,'20220127','20220426');
insert into insurance (ins_id, grnt_cd, poli_no)
values('test1ins1','I000201','test1');
insert into insurance (ins_id, grnt_cd, poli_no)
values('test1ins2','I000202','test1');
insert into insurance (ins_id, grnt_cd, poli_no)
values('test2ins1','I000201','test2');