const API='http://localhost:8080';
const $=x=>document.getElementById(x), esc=x=>String(x??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const arr=x=>Array.isArray(x)?x:(x?.data||x?.content||x?.items||[]);
async function api(path,opt={}){let r=await fetch(API+path,{headers:{'Content-Type':'application/json'},...opt});if(!r.ok)throw Error(await r.text()||('HTTP '+r.status));let t=r.headers.get('content-type')||'';return t.includes('json')?r.json():r.text()}
function msg(t,ok=true){let d=document.createElement('div');d.className='toast '+(ok?'ok':'err');d.textContent=t;$('toast').appendChild(d);setTimeout(()=>d.remove(),3000)}
function landing(){$('landing').classList.remove('hidden');$('auth').classList.add('hidden');$('app').classList.add('hidden')}
function auth(type){$('landing').classList.add('hidden');$('auth').classList.remove('hidden');$('app').classList.add('hidden');$('login').classList.toggle('hidden',type!=='login');$('register').classList.toggle('hidden',type!=='register')}
async function register() {
    let n = $('rn').value.trim();
    let e = $('re').value.trim();
    let p = $('rp').value;

    if (!n || !e || p.length < 6) {
        return msg(
            'Enter valid name, email and 6+ character password.',
            false
        );
    }

    try {
        let r = await api('/api/customers', {
            method: 'POST',
            body: JSON.stringify({
                fullName: n,
                email: e,
                address: '',
                password: p
            })
        });

        if (r?.success === false) {
            return msg(r.message || 'Account creation failed.', false);
        }

        $('le').value = e;
        $('lp').value = '';

        msg('Account created successfully');
        auth('login');

    } catch (e) {
        msg(e.message || 'Account creation failed.', false);
    }
}
async function login() {

    let e = $('le').value.trim();
    let p = $('lp').value;

    if (!e || !p) {
        return msg(
            'Enter email and password.',
            false
        );
    }

    try {

        let r = await api('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({
                email: e,
                password: p
            })
        });

        if (!r.success) {
            return msg(
                r.message || 'Invalid email or password.',
                false
            );
        }

        let u = {
            n: r.fullName,
            e: r.email,
            customerId: r.customerId
        };

        localStorage.user = JSON.stringify(u);

        enter(u);

        msg('Login successful');

    } catch (e) {

        msg(
            e.message || 'Login failed.',
            false
        );
    }
}
function enter(u){$('landing').classList.add('hidden');$('auth').classList.add('hidden');$('app').classList.remove('hidden');$('uname').textContent=$('welcome').textContent=$('pn').textContent=u.n;$('pe').textContent=u.e;$('av').textContent=$('pa').textContent=u.n[0].toUpperCase();openSec('home');dashboard()}
function logout(){localStorage.removeItem('user');landing();msg('Logged out')}
function theme(){document.body.classList.toggle('dark');let d=document.body.classList.contains('dark');localStorage.theme=d?'dark':'light';document.querySelectorAll('.theme').forEach(x=>x.textContent=d?'☀️':'🌙');$('tm').textContent=d?'Light Mode':'Dark Mode'}
function openSec(n,b){document.querySelectorAll('.sec').forEach(x=>x.classList.remove('active'));$(n).classList.add('active');document.querySelectorAll('.side button[data-s]').forEach(x=>x.classList.remove('active'));(b||document.querySelector(`[data-s="${n}"]`))?.classList.add('active');$('title').textContent={home:'Dashboard',customers:'Customers',mobiles:'Mobile Numbers',plans:'Recharge Plans',recharge:'New Recharge',history:'Recharge History',average:'Above Average Plans',profile:'Profile'}[n];document.querySelector('aside').classList.remove('open');if(n==='customers')customers();if(n==='mobiles')mobiles();if(n==='plans')plans();if(n==='recharge'){mobileSelect();planSelect()}if(n==='history')historyData();if(n==='average')average()}
async function dashboard(){for(let [url,id] of [['/api/customers','sc'],['/api/mobiles','sm'],['/api/plans','sp'],['/api/recharges','sr']])try{$(id).textContent=arr(await api(url)).length}catch{$(id).textContent='—'}}
async function customers(){
    try{
        let d = arr(await api('/api/customers'));

        $('ct').innerHTML = d.map(x => `
            <tr>
                <td>${esc(x.customerId ?? x.id)}</td>
                <td>${esc(x.customerName ?? x.fullName ?? x.name)}</td>
                <td>${esc(x.email)}</td>
            </tr>
        `).join('') || '<tr><td colspan="3">No customers</td></tr>';

    }catch(e){
        $('ct').innerHTML =
            '<tr><td colspan="3">' + esc(e.message) + '</td></tr>';
    }
}
async function mobiles(){try{let d=arr(await api('/api/mobiles'));$('mt').innerHTML=d.map(x=>`<tr><td>${esc(x.mobileId??x.id)}</td><td>${esc(x.customerName ?? x.fullName ?? x.customerId)}</td><td>${esc(x.mobileNumber??x.number)}</td><td>${esc(x.operatorName??x.operator)}</td><td>${esc(x.validityEndDate??'-')}</td><td>${esc(x.status??x.mobileStatus??'ACTIVE')}</td></tr>`).join('')}catch(e){$('mt').innerHTML='<tr><td colspan=6>'+esc(e.message)+'</td></tr>'}}
async function plans(){try{let d=arr(await api('/api/plans'));$('pg').innerHTML=d.map(x=>`<div class=plan>📶<h3>${esc(x.planName??x.name)}</h3><p class=muted>${esc(x.description??'Recharge plan')}</p><div class=price>₹${esc(x.amount??x.price)}</div><small>Validity: ${esc(x.validityDays??x.validity??'-')} days</small></div>`).join('')}catch(e){$('pg').textContent=e.message}}
async function historyData(){try{let d=arr(await api('/api/recharges'));$('ht').innerHTML=d.map(x=>`<tr><td>${esc(x.rechargeId??x.id)}</td><td>${esc(x.customerName)}</td><td>${esc(x.mobileNumber)}</td><td>${esc(x.operatorName)}</td><td>${esc(x.planName)}</td><td>₹${esc(x.amount)}</td><td>${esc(x.rechargeDate)}</td><td>${esc(x.paymentMethod)}</td><td>${esc(x.rechargeStatus??'SUCCESS')}</td></tr>`).join('')}catch(e){$('ht').innerHTML='<tr><td colspan=9>'+esc(e.message)+'</td></tr>'}}
async function average(){try{let d=arr(await api('/api/plans/above-average-usage'));$('at').innerHTML=d.map(x=>`<tr><td>${esc(x.planId??x.id)}</td><td>${esc(x.planName??x.name)}</td><td>${esc(x.usageCount??x.count)}</td></tr>`).join('')}catch(e){$('at').innerHTML='<tr><td colspan=3>Check your existing above-average endpoint.</td></tr>'}}
async function mobileModal(){ $('modal').classList.remove('hidden');try{let d=arr(await api('/api/customers'));$('mc').innerHTML='<option value="">Select Customer</option>'+d.map(x=>`<option value="${esc(x.customerId??x.id)}">${esc(x.customerName ?? x.fullName ?? x.name)}</option>`).join('')}catch{msg('Cannot load customers',false)}}
function closeModal(){$('modal').classList.add('hidden')}
async function addMobile(){let c=$('mc').value,n=$('mn').value.trim(),o=$('mo').value;if(!c||!/^[0-9]{10}$/.test(n))return msg('Select customer and enter valid 10-digit number.',false);try{await api('/api/mobiles',{method:'POST',body:JSON.stringify({customerId:Number(c),mobileNumber:n,operatorName:o})});closeModal();msg('Mobile number added successfully');mobiles();dashboard()}catch(e){msg(e.message,false)}}
async function mobileSelect(){try{let d=arr(await api('/api/mobiles'));$('rm').innerHTML='<option value="">Select Mobile Number</option>'+d.map(x=>`<option value="${esc(x.mobileId??x.id)}">${esc(x.mobileNumber??x.number)} — ${esc(x.customerName??'')}</option>`).join('')}catch{}}
async function planSelect(){try{let d=arr(await api('/api/plans'));$('rp2').innerHTML='<option value="">Select Plan</option>'+d.map(x=>`<option value="${esc(x.planId??x.id)}">${esc(x.planName??x.name)} — ₹${esc(x.amount??x.price)}</option>`).join('')}catch{}}
async function recharge(){let m=$('rm').value,p=$('rp2').value,pay=$('pay').value;if(!m||!p)return msg('Select mobile and plan.',false);try{let r=await api('/api/recharges',{method:'POST',body:JSON.stringify({mobileId:Number(m),planId:Number(p),paymentMethod:pay})});msg(r?.transactionRef||r?.transaction_ref||'Recharge successful');historyData();dashboard()}catch(e){msg(e.message,false)}}
if(localStorage.theme==='dark')theme();if(localStorage.user)try{enter(JSON.parse(localStorage.user))}catch{landing()}
